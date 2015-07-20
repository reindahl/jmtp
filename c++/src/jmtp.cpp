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
#include <atlbase.h>
#include <jni.h>

#include "jmtp.h"

void ThrowCOMException(JNIEnv* env, LPWSTR message, HRESULT hr)
{
	jclass cls;
	jmethodID mid;
	jstring jsMessage;
	jobject exception;

	cls = env->FindClass("be/derycke/pieter/com/COMException");
	mid = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;I)V");
	jsMessage = env->NewString((jchar*)message, wcslen(message));
	exception = env->NewObject(cls, mid, jsMessage, (jint)hr);
	env->Throw((jthrowable)exception);
}

jobject ConvertGuidToJava(JNIEnv* env, GUID guid)
{
	jclass cls;
	jmethodID mid;
	jshortArray jData4;

	cls = env->FindClass("be/derycke/pieter/com/Guid");
	mid = env->GetMethodID(cls, "<init>", "(JII[S)V");
	
	jData4 = env->NewShortArray(8);
	for(int i = 0; i < 8; i++) {
		jshort jsBuffer = guid.Data4[i];
		env->SetShortArrayRegion(jData4, i, 1, &jsBuffer);
	}

	return env->NewObject(cls, mid, (jlong)guid.Data1, (jint)guid.Data2, (jint)guid.Data3, jData4);
}

GUID ConvertJavaToGuid(JNIEnv* env, jobject jGuid)
{
	GUID guid;
	jmethodID mid;
	jclass cls;

	cls = env->GetObjectClass(jGuid);

	mid	= env->GetMethodID(cls, "getData1", "()J");
	guid.Data1 = static_cast<unsigned long>(env->CallLongMethod(jGuid, mid));

	mid = env->GetMethodID(cls, "getData2", "()I");
	guid.Data2 = static_cast<unsigned short>(env->CallIntMethod(jGuid, mid));

	mid = env->GetMethodID(cls, "getData3", "()I");
	guid.Data3 = static_cast<unsigned short>(env->CallIntMethod(jGuid, mid));

	mid = env->GetMethodID(cls, "getData4", "()[S");
	jshort* data4 = env->GetShortArrayElements((jshortArray)env->CallObjectMethod(jGuid, mid), NULL);
	for(int i = 0; i < 8; i++)
		guid.Data4[i] = static_cast<unsigned char>(data4[i]);

	return guid;
}

jobject ConvertPropertyKeyToJava(JNIEnv* env, PROPERTYKEY key)
{
	jclass cls;
	jmethodID mid;

	cls = env->FindClass("jmtp/PropertyKey");
	mid = env->GetMethodID(cls, "<init>", "(Lbe/derycke/pieter/com/Guid;J)V");

	return env->NewObject(cls, mid, ConvertGuidToJava(env, key.fmtid), (jlong)key.pid);
}

PROPERTYKEY ConvertJavaToPropertyKey(JNIEnv* env, jobject jKey)
{
	PROPERTYKEY key;
	jmethodID mid;
	jclass cls;
	jobject jGuid;

	cls = env->FindClass("jmtp/PropertyKey");

	mid = env->GetMethodID(cls, "getPid", "()J");
	key.pid = static_cast<DWORD>(env->CallLongMethod(jKey, mid));

	mid = env->GetMethodID(cls, "getFmtid", "()Lbe/derycke/pieter/com/Guid;");
	jGuid = env->CallObjectMethod(jKey, mid);
	key.fmtid = ConvertJavaToGuid(env, jGuid);

	return key;
}

jobject ConvertPropVariantToJava(JNIEnv* env, PROPVARIANT pv)
{
	//variabelen
	jclass cls;
	jmethodID mid;
	jstring jsValue;

	if(pv.vt == VT_LPWSTR) {
		//methode implementatie
		cls = env->FindClass("jmtp/PropVariant");
		mid = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;)V");
		jsValue = env->NewString((jchar*)pv.pwszVal, wcslen(pv.pwszVal));
		return env->NewObject(cls, mid, jsValue);
	}
	else
		return NULL;	//andere types dan string in de propvariant worden momenteel niet ondersteunt
}

//nog werk nodig! met de union!!
PROPVARIANT ConvertJavaToPropVariant(JNIEnv* env, jobject jobjPropVariant)
{
	//variabelen
	jclass cls;
	jmethodID mid;
	PROPVARIANT pv;
	jstring jsValue;
	LPWSTR wszBuffer;
	LPWSTR wszValue;
	jobject jobjObjectValue;


	//methode implementatie
	PropVariantInit(&pv);
	cls = env->FindClass("jmtp/PropVariant");
	
	mid = env->GetMethodID(cls, "getVt", "()I");
	pv.vt = static_cast<VARTYPE>(env->CallIntMethod(jobjPropVariant, mid));

	switch(pv.vt)
	{
		case VT_LPWSTR:
			mid = env->GetMethodID(cls, "getValue", "()Ljava/lang/Object;");
			jsValue = (jstring)env->CallObjectMethod(jobjPropVariant, mid);
			wszBuffer = (WCHAR*)env->GetStringChars(jsValue, NULL);
			wszValue = (WCHAR*)CoTaskMemAlloc(sizeof(WCHAR) * (wcslen(wszBuffer) + 1));
			wcscpy_s(wszValue, wcslen(wszBuffer) + 1, wszBuffer);
			env->ReleaseStringChars(jsValue, (jchar*)wszBuffer);
			pv.pwszVal = wszValue;
			break;
		case VT_BOOL:
			mid = env->GetMethodID(cls, "getValue", "()Ljava/lang/Object;");
			jobjObjectValue = env->CallObjectMethod(jobjPropVariant, mid);
			mid = env->GetMethodID(env->FindClass("java/lang/Boolean"), "booleanValue", "()Z");
			pv.boolVal = env->CallBooleanMethod(jobjObjectValue, mid);
			break;
	}
	//andere types worden momenteel niet ondersteunt
	
	return pv;
}

ULONGLONG ConvertJavaToUnsignedLongLong(JNIEnv* env, jobject jobjBigInteger)
{
	jmethodID mid;
	jbyteArray jbaArray;
	jboolean isCopy = JNI_TRUE;
	jbyte* buffer;
	jsize jiLength;
	ULONGLONG result = 0;
	byte* tmp;

	mid = env->GetMethodID(env->FindClass("java/math/BigInteger"), "signum", "()I");
	if(env->CallIntMethod(jobjBigInteger, mid) >= 0)
	{
		mid = env->GetMethodID(env->FindClass("java/math/BigInteger"), "toByteArray", "()[B");
		jbaArray = (jbyteArray)env->CallObjectMethod(jobjBigInteger, mid);

		//van big endian naar little endian converteren
		jiLength = env->GetArrayLength(jbaArray);
		buffer = env->GetByteArrayElements(jbaArray, &isCopy);

		//i < 8 is omdat we met een 64 bit getal werken
		tmp = new byte[sizeof(ULONGLONG)];
		for(int i = 0; i < sizeof(ULONGLONG); i++)
		{
			if(i < jiLength)
				tmp[i] = buffer[jiLength - i - 1];
			else
				tmp[i] = 0;
		}
		memcpy_s(&result, sizeof(ULONGLONG), tmp, sizeof(ULONGLONG));
		delete tmp;

		env->ReleaseByteArrayElements(jbaArray, buffer, JNI_ABORT);

		return result;
	}
	else
	{
		env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Only positive integers can be converted.");
		return -1;	//-> wat doen we bij negatieve getallen?
	}
}

jobject ConvertUnsignedLongLongToJava(JNIEnv* env, ULONGLONG number)
{
	jbyteArray buffer;
	jclass cls;
	jmethodID mid;

	buffer = env->NewByteArray(9);
	for(int i = 0; i < 8; i++)
		env->SetByteArrayRegion(buffer, (i + 1), 1, (jbyte*)((byte*)&number + 7 - i));

	cls = env->FindClass("java/math/BigInteger"); 
	mid = env->GetMethodID(cls, "<init>", "([B)V");
	return env->NewObject(cls, mid, buffer);
}

inline jlong ConvertComReferenceToPointer(JNIEnv* env, jobject jobjReference)
{
	jmethodID mid;

	mid = env->GetMethodID(env->FindClass("be/derycke/pieter/com/COMReference"), "getMemoryAddress", "()J");
	return env->CallLongMethod(jobjReference, mid);
}

//De COMReference opvragen van een COMReferencable object
inline jobject RetrieveCOMReferenceFromCOMReferenceable(JNIEnv* env, jobject jobjCOMReferenceable)
{
	jmethodID mid = env->GetMethodID(env->FindClass("be/derycke/pieter/com/COMReferenceable"), 
		"getReference", "()Lbe/derycke/pieter/com/COMReference;");
	return env->CallObjectMethod(jobjCOMReferenceable, mid);
}

jlong GetComReferencePointer(JNIEnv* env, jobject obj, const char* fieldName)
{
	jclass cls;
	jobject reference;

	cls = env->GetObjectClass(obj);
	reference = env->GetObjectField(obj, env->GetFieldID(cls, fieldName, "Lbe/derycke/pieter/com/COMReference;"));
	return ConvertComReferenceToPointer(env, reference);
}

jlong GetComReferencePointerFromComReferenceable(JNIEnv* env, jobject jobjCOMReferenceable)
{
	jobject jobjReference = RetrieveCOMReferenceFromCOMReferenceable(env, jobjCOMReferenceable);
	return ConvertComReferenceToPointer(env, jobjReference);
}