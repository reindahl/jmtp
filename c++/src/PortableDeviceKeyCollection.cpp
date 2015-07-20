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
#include "jmtp_PortableDeviceKeyCollectionImplWin32.h"

static inline IPortableDeviceKeyCollection* GetPortableDeviceKeyCollection(JNIEnv* env, jobject obj)
{
	return (IPortableDeviceKeyCollection*)GetComReferencePointer(env, obj, "pKeyCollection");
}

JNIEXPORT void JNICALL Java_jmtp_PortableDeviceKeyCollectionImplWin32_add
	(JNIEnv* env, jobject obj, jobject jobjKey)
{
	HRESULT hr;
	IPortableDeviceKeyCollection* pKeyCollection;

	if(jobjKey == NULL)
	{
		env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "key can't be null");
		return;
	}
	else
	{
		pKeyCollection = GetPortableDeviceKeyCollection(env, obj);
		hr = pKeyCollection->Add(ConvertJavaToPropertyKey(env, jobjKey));
		if(FAILED(hr))
		{
			ThrowCOMException(env, L"Failed to add the key to the collection", hr);
			return;
		}
	}
}

JNIEXPORT void JNICALL Java_jmtp_PortableDeviceKeyCollectionImplWin32_clear
	(JNIEnv* env, jobject obj)
{
	HRESULT hr;
	IPortableDeviceKeyCollection* pKeyCollection;

	pKeyCollection = GetPortableDeviceKeyCollection(env, obj);
	hr = pKeyCollection->Clear();
	if(FAILED(hr))
	{
		ThrowCOMException(env, L"Failed to clear the collection", hr);
		return;
	}
}

JNIEXPORT jlong JNICALL Java_jmtp_PortableDeviceKeyCollectionImplWin32_count
	(JNIEnv* env, jobject obj)
{
	HRESULT hr;
	IPortableDeviceKeyCollection* pKeyCollection;
	DWORD dwCount;

	pKeyCollection = GetPortableDeviceKeyCollection(env, obj);
	hr = pKeyCollection->GetCount(&dwCount);
	if(FAILED(hr))
	{
		ThrowCOMException(env, L"Failed to count the collection", hr);
		return -1;
	}

	return dwCount;
}

JNIEXPORT jobject JNICALL Java_jmtp_PortableDeviceKeyCollectionImplWin32_getAt
	(JNIEnv* env, jobject obj, jlong jlPosition)
{
	HRESULT hr;
	IPortableDeviceKeyCollection* pKeyCollection;
	PROPERTYKEY key;
	DWORD dwCount;

	pKeyCollection = GetPortableDeviceKeyCollection(env, obj);
	hr = pKeyCollection->GetCount(&dwCount);
	if(FAILED(hr))
	{
		ThrowCOMException(env, L"Failed to count the collection", hr);
		return NULL;
	}

	if(jlPosition < dwCount && jlPosition >= 0)
	{
		hr = pKeyCollection->GetAt(static_cast<DWORD>(jlPosition), &key);
		if(FAILED(hr))
		{
			ThrowCOMException(env, L"Failed to retrieve the specified element of the collection", hr);
			return NULL;
		}
	}
	else
	{
		env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "Invalid index");
		return NULL;
	}

	return ConvertPropertyKeyToJava(env, key);
}

JNIEXPORT void JNICALL Java_jmtp_PortableDeviceKeyCollectionImplWin32_removeAt
	(JNIEnv* env, jobject obj, jlong jlPosition)
{
	HRESULT hr;
	IPortableDeviceKeyCollection* pKeyCollection;
	DWORD dwCount;

	pKeyCollection = GetPortableDeviceKeyCollection(env, obj);
	hr = pKeyCollection->GetCount(&dwCount);
	if(FAILED(hr))
	{
		ThrowCOMException(env, L"Failed to count the collection", hr);
		return;
	}

	if(jlPosition < dwCount && jlPosition >= 0)
	{
		hr = pKeyCollection->RemoveAt(static_cast<DWORD>(jlPosition));
		if(FAILED(hr))
		{
			ThrowCOMException(env, L"Failed to remove the specified element from the collection", hr);
			return;
		}
	}
	else
	{
		env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "Invalid index");
	}
}