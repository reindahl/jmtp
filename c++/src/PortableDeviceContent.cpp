/*
 * Copyright 2007 Pieter De Rycke
 * 
 * This file is part of JMTP.
 * 
 * JTMP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of 
 * the License, or any later version.
 * 
 * JMTP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU LesserGeneral Public 
 * License along with JMTP. If not, see <http://www.gnu.org/licenses/>.
 */

#include <PortableDeviceApi.h>
#include <PortableDevice.h>
#include <list>

#include "jmtp.h"
#include "jmtp_PortableDeviceContentImplWin32.h"


#include <string>
#include <atlbase.h>
#include <atlstr.h>

#include <iostream>


using namespace std;

static inline IPortableDeviceContent* GetPortableDeviceContent(JNIEnv* env, jobject obj)
{
	return (IPortableDeviceContent*)GetComReferencePointer(env, obj, "pDeviceContent");
}

JNIEXPORT jstring JNICALL Java_jmtp_PortableDeviceContentImplWin32_createObjectWithPropertiesAndData
	(JNIEnv* env, jobject obj, jobject jobjValues, jobject jobjFile)
{
	//Variables
	HRESULT hr;
	IPortableDeviceContent* pDeviceContent;
	IPortableDeviceValues* pDeviceObjectValues;
	jobject jobjValuesReference;
	jstring jsFileLocation;
	LPWSTR wszFileLocation;
	DWORD dwBufferSize;
	CComPtr<IStream> pFileStream;
	CComPtr<IStream> pDeviceStream;
	CComPtr<IPortableDeviceDataStream> pDeviceDataStream;
	STATSTG fileStats;
	BYTE* pBuffer;
	DWORD dwReadFromStream;
	LPWSTR wszObjectID;
	jstring jsObjectID;
	jmethodID mid;


	//Methode implementation
	pDeviceContent = GetPortableDeviceContent(env, obj);
	jobjValuesReference = RetrieveCOMReferenceFromCOMReferenceable(env, jobjValues);
	pDeviceObjectValues = (IPortableDeviceValues*)ConvertComReferenceToPointer(env, jobjValuesReference);
	
	//COM stream object create
	mid = env->GetMethodID(env->FindClass("java/io/File"), "getAbsolutePath", "()Ljava/lang/String;");
	jsFileLocation = (jstring)env->CallObjectMethod(jobjFile, mid);
	wszFileLocation = (WCHAR*)env->GetStringChars(jsFileLocation, nullptr);
	hr = SHCreateStreamOnFileW(wszFileLocation, STGM_READ, &pFileStream);
	env->ReleaseStringChars(jsFileLocation, (jchar*)wszFileLocation); //string resources terug vrijgeven
	cout << "test" << endl;
	if(SUCCEEDED(hr))
	{
		// determine size of the file
		//(due to a limitation in Java in the area of unsigned integers, we have to do it in c ++)
		pFileStream->Stat(&fileStats, STATFLAG_NONAME);
		pDeviceObjectValues->SetUnsignedLargeIntegerValue(WPD_OBJECT_SIZE, fileStats.cbSize.QuadPart);

		hr = pDeviceContent->CreateObjectWithPropertiesAndData(pDeviceObjectValues, &pDeviceStream, &dwBufferSize, nullptr);
		cout << "test2" << endl;
		if(SUCCEEDED(hr))
		{
			pDeviceStream->QueryInterface(IID_IPortableDeviceDataStream, (void**)&pDeviceDataStream);

			//copying data
			pBuffer = new BYTE[dwBufferSize];
			dwReadFromStream = 0;
			do
			{
				pFileStream->Read(pBuffer, dwBufferSize, &dwReadFromStream);
				pDeviceDataStream->Write(pBuffer, dwReadFromStream, nullptr);
			}
			while(dwReadFromStream > 0);
			delete[] pBuffer;
			hr = pDeviceDataStream->Commit(STGC_DEFAULT);
			cout << "test3" << endl;
			if(SUCCEEDED(hr))
			{
				pDeviceDataStream->GetObjectID(&wszObjectID);
				jsObjectID = (jstring)env->NewString((jchar*)wszObjectID, wcslen(wszObjectID));
				CoTaskMemFree(wszObjectID);
				return jsObjectID;
			}
			else
			{
				ThrowCOMException(env, L"Couldn't commit the data to the portable device", hr);
			}
		}
		else
		{
			ThrowCOMException(env, L"Couldn't create a COM stream object to the portable device", hr);
		}
	}
	else
	{
		ThrowCOMException(env, L"Couldn't create a COM stream object to the file", hr);
	}

	return NULL;
}

JNIEXPORT jstring JNICALL Java_jmtp_PortableDeviceContentImplWin32_createObjectWithPropertiesOnly
	(JNIEnv* env, jobject obj, jobject jobjValues)
{
	//variabelen
	HRESULT hr;
	IPortableDeviceContent* pDeviceContent;
	IPortableDeviceValues* pValues;
	LPWSTR wszObjectID;
	jstring jsObjectID;

	
	//Methode implementatie
	if(jobjValues != NULL)
	{
		pDeviceContent = GetPortableDeviceContent(env, obj);
		pValues = (IPortableDeviceValues*)GetComReferencePointerFromComReferenceable(env, jobjValues);
		hr = pDeviceContent->CreateObjectWithPropertiesOnly(pValues, &wszObjectID);

		if(SUCCEEDED(hr))
		{
			jsObjectID = (jstring)env->NewString((jchar*)wszObjectID, wcslen(wszObjectID));
			CoTaskMemFree(wszObjectID);
			return jsObjectID;
		}
		else
		{
			ThrowCOMException(env, L"Couldn't create the file", hr);
			return NULL;
		}
	}
	else
	{
		env->ThrowNew(env->FindClass("java/lang/NullPointerException"), "values can't be null");
		return NULL;
	}
}

JNIEXPORT void JNICALL Java_jmtp_PortableDeviceContentImplWin32_delete
	(JNIEnv* env, jobject obj, jint jiOptions, jobject jobjObjectIDs)
{
	//variabelen
	HRESULT hr;
	IPortableDeviceContent* pDeviceContent;
	IPortableDevicePropVariantCollection* pObjectIDs;
	

	//Methode implementatie
	if(jiOptions == 0 || jiOptions == 1)
	{
		pDeviceContent = GetPortableDeviceContent(env, obj);
		pObjectIDs = (IPortableDevicePropVariantCollection*)GetComReferencePointerFromComReferenceable(env, jobjObjectIDs);
		hr = pDeviceContent->Delete(jiOptions, pObjectIDs, NULL);

		if(FAILED(hr))
		{
			ThrowCOMException(env, L"Failed to delete the files", hr);
			return;
		}
	}
	else
	{
		env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "The parameter options has an invalid value.");
		return;
	}
}

JNIEXPORT jobjectArray JNICALL Java_jmtp_PortableDeviceContentImplWin32_listChildObjects
	(JNIEnv* env, jobject obj, jstring jsParentID)
{
	//variabelen
	HRESULT hr;
	IPortableDeviceContent* pDeviceContent;
	LPWSTR wszParentID;
	CComPtr<IEnumPortableDeviceObjectIDs> pEnum;
	std::list<LPWSTR> childList;
	std::list<LPWSTR>::iterator iterator;
	LPWSTR* wszObjectID;
	ULONG fetched;
	jobjectArray jobjaChildArray;


	//Methode implementatie
	pDeviceContent = GetPortableDeviceContent(env, obj);
	wszParentID = (WCHAR*)env->GetStringChars(jsParentID, NULL);
	hr = pDeviceContent->EnumObjects(0, wszParentID, NULL, &pEnum);
	env->ReleaseStringChars(jsParentID, (jchar*)wszParentID);
	
	if(SUCCEEDED(hr))
	{
		wszObjectID = new LPWSTR[1];
		while(SUCCEEDED(pEnum->Next(1, wszObjectID, &fetched)) && fetched > 0)
		{
			childList.push_back(wszObjectID[0]);
		}
		delete[] wszObjectID;

		jobjaChildArray = env->NewObjectArray(childList.size(), env->FindClass("Ljava/lang/String;"), NULL);
		int i = 0;
		for (iterator = childList.begin(); iterator != childList.end(); iterator++)
		{
			env->SetObjectArrayElement(jobjaChildArray, i, env->NewString((jchar*)*iterator, wcslen(*iterator)));
			CoTaskMemFree(*iterator);
			i++;
		}

		return jobjaChildArray;
	}
	else
	{
		ThrowCOMException(env, L"Failed to retrieve the enumerator", hr);
	}
	
	return NULL;
}

JNIEXPORT jobject JNICALL Java_jmtp_PortableDeviceContentImplWin32_getProperties
	(JNIEnv* env, jobject obj)
{
	//variabelen
	HRESULT hr;
	IPortableDeviceContent* pContent;
	IPortableDeviceProperties* pProperties;
	jclass cls;
	jmethodID mid;
	jobject jobjReference;


	//Methode implementatie
	pContent = GetPortableDeviceContent(env, obj);
	hr = pContent->Properties(&pProperties);

	if(SUCCEEDED(hr))
	{
		//smart reference object create
		cls = env->FindClass("be/derycke/pieter/com/COMReference");
		mid = env->GetMethodID(cls, "<init>", "(J)V");
		jobjReference = env->NewObject(cls, mid, pProperties);
		
		cls = env->FindClass("jmtp/PortableDevicePropertiesImplWin32");
		mid = env->GetMethodID(cls, "<init>", "(Lbe/derycke/pieter/com/COMReference;)V");
		return env->NewObject(cls, mid, jobjReference);
	}
	else
	{
		ThrowCOMException(env, L"Failed to retrieve the property object", hr);
		return NULL;
	}
}

JNIEXPORT jobject JNICALL Java_jmtp_PortableDeviceContentImplWin32_getObjectIDsFromPersistentUniqueIDs
	(JNIEnv* env, jobject obj, jobject jobjPropVariantCollection)
{
	//variabelen
	HRESULT hr;
	IPortableDeviceContent* pContent;
	IPortableDevicePropVariantCollection* pPersistentUniqueIDs;
	IPortableDevicePropVariantCollection* pObjectIDs;
	jclass cls;
	jmethodID mid;
	jobject jobjReference;

	//Methode implementatie
	if(jobjPropVariantCollection != NULL)
	{
		pContent = GetPortableDeviceContent(env, obj);
		pPersistentUniqueIDs =
			(IPortableDevicePropVariantCollection*)GetComReferencePointerFromComReferenceable(env, jobjPropVariantCollection);

		hr = pContent->GetObjectIDsFromPersistentUniqueIDs(pPersistentUniqueIDs, &pObjectIDs);

		if(SUCCEEDED(hr))
		{
			//smart reference object aanmaken
			cls = env->FindClass("be/derycke/pieter/com/COMReference");
			mid = env->GetMethodID(cls, "<init>", "(J)V");
			jobjReference = env->NewObject(cls, mid, pObjectIDs);
			
			cls = env->FindClass("jmtp/PortableDevicePropVariantCollectionImplWin32");
			mid = env->GetMethodID(cls, "<init>", "(Lbe/derycke/pieter/com/COMReference;)V");
			return env->NewObject(cls, mid, jobjReference);
		}
		else
		{
			ThrowCOMException(env, L"Failed to retrieve the ObjectIDs", hr);
			return NULL;
		}
	}
	else
	{
		env->ThrowNew(env->FindClass("java/lang/NullPointerException"), "persistentUniqueIDs can't be null");
		return NULL;
	}
}






// Copies data from a source stream to a destination stream using the
// specified cbTransferSize as the temporary buffer size.
HRESULT StreamCopy(IStream *pDestStream, IStream *pSourceStream, DWORD cbTransferSize, DWORD *pcbWritten)
{
	HRESULT hr = S_OK;

	// Allocate a temporary buffer (of Optimal transfer size) for the read results to
	// be written to.
	BYTE*   pObjectData = new (std::nothrow) BYTE[cbTransferSize];
	if (pObjectData != NULL)
	{
		DWORD cbTotalBytesRead = 0;
		DWORD cbTotalBytesWritten = 0;

		DWORD cbBytesRead = 0;
		DWORD cbBytesWritten = 0;

		// Read until the number of bytes returned from the source stream is 0, or
		// an error occured during transfer.
		do
		{
			// Read object data from the source stream
			hr = pSourceStream->Read(pObjectData, cbTransferSize, &cbBytesRead);
			if (FAILED(hr))
			{
				printf("! Failed to read %d bytes from the source stream, hr = 0x%lx\n", cbTransferSize, hr);
			}

			// Write object data to the destination stream
			if (SUCCEEDED(hr))
			{
				cbTotalBytesRead += cbBytesRead; // Calculating total bytes read from device for debugging purposes only

				hr = pDestStream->Write(pObjectData, cbBytesRead, &cbBytesWritten);
				if (FAILED(hr))
				{
					printf("! Failed to write %d bytes of object data to the destination stream, hr = 0x%lx\n", cbBytesRead, hr);
				}

				if (SUCCEEDED(hr))
				{
					cbTotalBytesWritten += cbBytesWritten; // Calculating total bytes written to the file for debugging purposes only
				}
			}

			// Output Read/Write operation information only if we have received data and if no error has occured so far.
			if (SUCCEEDED(hr) && (cbBytesRead > 0))
			{
				//printf("Read %d bytes from the source stream...Wrote %d bytes to the destination stream...\n", cbBytesRead, cbBytesWritten);
			}

		} while (SUCCEEDED(hr) && (cbBytesRead > 0));

		// If the caller supplied a pcbWritten parameter and we
		// and we are successful, set it to cbTotalBytesWritten
		// before exiting.
		if ((SUCCEEDED(hr)) && (pcbWritten != NULL))
		{
			*pcbWritten = cbTotalBytesWritten;
		}

		// Remember to delete the temporary transfer buffer
		delete[] pObjectData;
		pObjectData = NULL;
	}
	else
	{
		printf("! Failed to allocate %d bytes for the temporary transfer buffer.\n", cbTransferSize);
	}

	return hr;
}

// Reads a string property from the IPortableDeviceProperties
// interface and returns it in the form of a CAtlStringW
HRESULT GetStringValue(IPortableDeviceProperties* pProperties, PCWSTR pszObjectID, REFPROPERTYKEY key, CAtlStringW &strStringValue)
{
	CComPtr<IPortableDeviceValues>        pObjectProperties;
	CComPtr<IPortableDeviceKeyCollection> pPropertiesToRead;

	// 1) CoCreate an IPortableDeviceKeyCollection interface to hold the the property key we wish to read.
	HRESULT hr = CoCreateInstance(CLSID_PortableDeviceKeyCollection, NULL, CLSCTX_INPROC_SERVER, IID_PPV_ARGS(&pPropertiesToRead));

	// 2) Populate the IPortableDeviceKeyCollection with the keys we wish to read.
	// NOTE: We are not handling any special error cases here so we can proceed with
	// adding as many of the target properties as we can.
	if (SUCCEEDED(hr))
	{
		if (pPropertiesToRead != NULL)
		{
			HRESULT hrTemp = S_OK;
			hrTemp = pPropertiesToRead->Add(key);

			if (FAILED(hrTemp))
				printf("! Failed to add PROPERTYKEY to IPortableDeviceKeyCollection, hr= 0x%lx\n", hrTemp);
		}
	}

	// 3) Call GetValues() passing the collection of specified PROPERTYKEYs.
	if (SUCCEEDED(hr))
		hr = pProperties->GetValues(pszObjectID, pPropertiesToRead, &pObjectProperties);

	// 4) Extract the string value from the returned property collection
	if (SUCCEEDED(hr))
	{
		PWSTR pszStringValue = NULL;
		hr = pObjectProperties->GetStringValue(key, &pszStringValue);

		if (SUCCEEDED(hr))
			strStringValue = pszStringValue; // assign the newly read string to the CAtlStringW out parameter
		else
			printf("! Failed to find property in IPortableDeviceValues, hr = 0x%lx\n", hr);

		CoTaskMemFree(pszStringValue);
		pszStringValue = NULL;
	}

	return hr;
}

std::wstring JavaToWSZ(JNIEnv* env, jstring string)
{
	std::wstring value;

	const jchar *raw = env->GetStringChars(string, 0);
	jsize len = env->GetStringLength(string);
	const jchar *temp = raw;

	value.assign(raw, raw + len);

	env->ReleaseStringChars(string, raw);

	return value;
}


// JNIEXPORT void JNICALL CopyFromPortableDeviceToHost(const WCHAR *, IPortableDevice *pDevice)
JNIEXPORT void JNICALL Java_jmtp_PortableDeviceContentImplWin32_copyFromPortableDeviceToHost(JNIEnv * env, jobject jobj, jstring jstr, jstring jpath)
{
	wstring szSelection = JavaToWSZ(env, jstr);

	wstring dir = JavaToWSZ(env, jpath);

	//printf("%ls\n\n",szSelection.c_str());
	//wcout << szSelection << endl << endl;

	HRESULT hr = S_OK;
	CComPtr<IPortableDeviceContent> pContent;
	CComPtr<IPortableDeviceResources> pResources;
	CComPtr<IPortableDeviceProperties> pProperties;
	CComPtr<IStream> pObjectDataStream;
	CComPtr<IStream> pFinalFileStream;
	DWORD cbOptimalTransferSize = 0;
	CAtlStringW strOriginalFileName;

	pContent = GetPortableDeviceContent(env, jobj);

	if (SUCCEEDED(hr))
	{
		hr = pContent->Transfer(&pResources);
		if (FAILED(hr))
			printf("! Failed to get IPortableDeviceResources from IPortableDeviceContent, hr = 0x%lx\n", hr);
	}


	if (SUCCEEDED(hr))
	{
		hr = pContent->Properties(&pProperties);
		if (SUCCEEDED(hr))
		{
			hr = GetStringValue(pProperties, szSelection.c_str(), WPD_OBJECT_ORIGINAL_FILE_NAME, strOriginalFileName);
			if (FAILED(hr))
			{
				printf("! Failed to read WPD_OBJECT_ORIGINAL_FILE_NAME on object '%ws', hr = 0x%lx\n", szSelection.c_str(), hr);
				strOriginalFileName.Format(L"%ws.data", szSelection.c_str());
				printf("* Creating a filename '%ws' as a default.\n", (PWSTR)strOriginalFileName.GetString());
				// set the HRESULT to S_OK, so we can continue
				// with our newly generated temporary file name
				hr = S_OK;
			}
		}
		else
		{
			printf("! Failed to get IPortableDeviceProperties from IPortableDeviceContent, hr = 0x%lx\n", hr);
		}
	}

	if (SUCCEEDED(hr))
	{
		hr = pResources->GetStream(szSelection.c_str(), WPD_RESOURCE_DEFAULT, STGM_READ, &cbOptimalTransferSize, &pObjectDataStream);
		if (FAILED(hr))
			printf("! Failed to get IStream (representing object data on the device) from IPortableDeviceResources, hr = 0x%lx\n", hr);
	}

	if (SUCCEEDED(hr))
	{
		std::wstring combined = dir;
		if (!combined.empty() && combined.back() != '\\')
			combined += '\\';
		combined += strOriginalFileName;

		hr = SHCreateStreamOnFileEx(combined.c_str(), STGM_CREATE | STGM_WRITE, 0, true, nullptr, &pFinalFileStream);
		if (FAILED(hr)) {
			// can be caused by creating a file where a folder with same name exist
			printf("! Failed to create a temporary file named (%ws) to transfer object (%ws), hr = 0x%lx\n", (PWSTR)strOriginalFileName.GetString(), szSelection.c_str(), hr);
		}
	}

	if (SUCCEEDED(hr))
	{
		DWORD cbTotalBytesWritten = 0;
		hr = StreamCopy(pFinalFileStream, pObjectDataStream, cbOptimalTransferSize, &cbTotalBytesWritten);

		if (FAILED(hr))
			printf("! Failed to transfer object from device, hr = 0x%lx\n", hr);
		//else
		//printf("* Transferred object '%ws' to '%ws'.\n", szSelection.c_str(), (PWSTR)strOriginalFileName.GetString());
	}

	fflush(stdout);
	//return hr;
}
