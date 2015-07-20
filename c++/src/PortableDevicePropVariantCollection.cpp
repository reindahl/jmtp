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

#include "jmtp.h"
#include "jmtp_PortableDevicePropVariantCollectionImplWin32.h"

static inline IPortableDevicePropVariantCollection* GetPortableDevicePropVariantCollection
	(JNIEnv* env, jobject obj)
{
	return (IPortableDevicePropVariantCollection*)GetComReferencePointer(env, obj, "pPropVariantCollection");
}

JNIEXPORT void JNICALL Java_jmtp_PortableDevicePropVariantCollectionImplWin32_add
	(JNIEnv* env, jobject obj, jobject jobjPropVariant)
{
	//variabelen
	HRESULT hr;
	IPortableDevicePropVariantCollection* pPropVariantCollection;
	PROPVARIANT pv;


	//methode implementatie
	if(jobjPropVariant != NULL)
	{
		pPropVariantCollection = GetPortableDevicePropVariantCollection(env, obj);
		pv = ConvertJavaToPropVariant(env, jobjPropVariant);
		hr = pPropVariantCollection->Add(&pv);
		PropVariantClear(&pv);

		if(FAILED(hr))
		{
			ThrowCOMException(env, L"Failed to add the element to the collection", hr);
			return;
		}
	}
	else
	{
		env->ThrowNew(env->FindClass("java/lang/NullPointerException"), 
			"propvariant isn't allowed to be null");
	}
}

JNIEXPORT void JNICALL Java_jmtp_PortableDevicePropVariantCollectionImplWin32_clear
	(JNIEnv* env, jobject obj)
{
	HRESULT hr;
	IPortableDevicePropVariantCollection* pPropVariantCollection;

	pPropVariantCollection = GetPortableDevicePropVariantCollection(env, obj);
	hr = pPropVariantCollection->Clear();
	if(FAILED(hr))
	{
		ThrowCOMException(env, L"Failed to clear the collection", hr);
		return;
	}
}

JNIEXPORT jlong JNICALL Java_jmtp_PortableDevicePropVariantCollectionImplWin32_count
	(JNIEnv* env, jobject obj)
{
	HRESULT hr;
	IPortableDevicePropVariantCollection* pPropVariantCollection;
	DWORD dwCount;

	pPropVariantCollection = GetPortableDevicePropVariantCollection(env, obj);
	hr = pPropVariantCollection->GetCount(&dwCount);
	if(SUCCEEDED(hr))
	{
		return (jlong)dwCount;
	}
	else
	{
		ThrowCOMException(env, L"Failed to count the collection", hr);
		return -1;
	}
}

JNIEXPORT jobject JNICALL Java_jmtp_PortableDevicePropVariantCollectionImplWin32_getAt
	(JNIEnv* env, jobject obj, jlong jlIndex)
{
	//variabelen
	HRESULT hr;
	IPortableDevicePropVariantCollection* pPropVariantCollection;
	PROPVARIANT pv;
	jobject jobjPropVariant;
	DWORD dwCount;


	//methode implementatie
	pPropVariantCollection = GetPortableDevicePropVariantCollection(env, obj);
	hr = pPropVariantCollection->GetCount(&dwCount);
	if(SUCCEEDED(hr))
	{
		if(jlIndex >= 0 && jlIndex < dwCount)
		{
			hr = pPropVariantCollection->GetAt((DWORD)jlIndex, &pv);
			if(SUCCEEDED(hr))
			{
				jobjPropVariant = ConvertPropVariantToJava(env, pv);
				PropVariantClear(&pv);
				return jobjPropVariant;
			}
			else
			{
				ThrowCOMException(env, L"Failed to retrieve the element from the collection", hr);
			}
		}
		else
		{
			env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "index out of range");
		}
	}
	else
	{
		ThrowCOMException(env, L"Failed to check the bounds of the collection", hr);
	}

	return NULL;
}

JNIEXPORT void JNICALL Java_jmtp_PortableDevicePropVariantCollectionImplWin32_removeAt
	(JNIEnv* env, jobject obj, jlong jlIndex)
{
	//variabelen
	HRESULT hr;
	IPortableDevicePropVariantCollection* pPropVariantCollection;
	DWORD dwCount;


	//methode implementatie
	pPropVariantCollection = GetPortableDevicePropVariantCollection(env, obj);
	hr = pPropVariantCollection->GetCount(&dwCount);
	if(SUCCEEDED(hr))
	{
		if(jlIndex >= 0 && jlIndex < dwCount)
		{
			hr = pPropVariantCollection->RemoveAt((DWORD)jlIndex);
			if(FAILED(hr))
			{
				ThrowCOMException(env, L"Failed to remove the element from the collection", hr);
				return;
			}
		}
		else
		{
			env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "index out of range");
		}
	}
	else
	{
		ThrowCOMException(env, L"Failed to check the bounds of the collection", hr);
	}
}