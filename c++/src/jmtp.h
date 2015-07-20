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

#pragma once

#include <objbase.h>
#include <atlbase.h>
#include <jni.h>

void ThrowCOMException(JNIEnv* env, LPWSTR message, HRESULT hr);

jobject ConvertGuidToJava(JNIEnv* env, GUID guid);
GUID ConvertJavaToGuid(JNIEnv* env, jobject jGuid);

jobject ConvertPropertyKeyToJava(JNIEnv* env, PROPERTYKEY key);
PROPERTYKEY ConvertJavaToPropertyKey(JNIEnv* env, jobject jKey);

jobject ConvertPropVariantToJava(JNIEnv* env, PROPVARIANT pv);
PROPVARIANT ConvertJavaToPropVariant(JNIEnv* env, jobject jobjPropVariant);

ULONGLONG ConvertJavaToUnsignedLongLong(JNIEnv* env, jobject jobjBigInteger);
jobject ConvertUnsignedLongLongToJava(JNIEnv* env, ULONGLONG number);

jobject RetrieveCOMReferenceFromCOMReferenceable(JNIEnv* env, jobject jobjCOMReferenceable);
jlong ConvertComReferenceToPointer(JNIEnv* env, jobject jobjReference);
jlong GetComReferencePointer(JNIEnv* env, jobject obj, const char* fieldName);
jlong GetComReferencePointerFromComReferenceable(JNIEnv* env, jobject jobjCOMReferenceable);