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

#include <objbase.h>
#include <PortableDeviceApi.h>

#include "jmtp.h"
#include "jmtp_PortableDeviceManagerImplWin32.h"

static inline IPortableDeviceManager* GetPortableDeviceManager(JNIEnv* env, jobject obj)
{
	return (IPortableDeviceManager*)GetComReferencePointer(env, obj, "pDeviceManager");
}

JNIEXPORT jobjectArray JNICALL Java_jmtp_PortableDeviceManagerImplWin32_getDevicesImpl
	(JNIEnv* env, jobject obj)
{
	HRESULT hr;
	IPortableDeviceManager* pDeviceManager;
	LPWSTR* deviceIDs;
	DWORD dwCount;
	jobjectArray jobjaDeviceIDs;
	
	pDeviceManager = GetPortableDeviceManager(env, obj);
	hr = pDeviceManager->GetDevices(NULL, &dwCount);
	if(FAILED(hr))
	{
		ThrowCOMException(env, L"Failed to count the connected devices", hr);
		return NULL;
	}
	else
	{
		deviceIDs = new LPWSTR[dwCount];
		hr = pDeviceManager->GetDevices(deviceIDs, &dwCount);
		if(SUCCEEDED(hr))
		{
			jobjaDeviceIDs = env->NewObjectArray(dwCount, env->FindClass("java/lang/String"), NULL);
			for(DWORD i = 0; i < dwCount; i++) {
				env->SetObjectArrayElement(jobjaDeviceIDs, i, env->NewString((jchar*)deviceIDs[i], wcslen(deviceIDs[i])));
				CoTaskMemFree(deviceIDs[i]);
			}
		}

		delete[] deviceIDs;

		if(FAILED(hr))
		{
			ThrowCOMException(env, L"Failed to count the connected devices", hr);
			return NULL;
		}

		return jobjaDeviceIDs;
	}
}

JNIEXPORT void JNICALL Java_jmtp_PortableDeviceManagerImplWin32_refreshDeviceListImpl
	(JNIEnv* env, jobject obj)
{
	HRESULT hr;
	IPortableDeviceManager* pDeviceManager;

	pDeviceManager = GetPortableDeviceManager(env, obj);
	hr = pDeviceManager->RefreshDeviceList();
	if(FAILED(hr))
	{
		ThrowCOMException(env, L"Failed to refresh the devicelist", hr);
		return;
	}
}