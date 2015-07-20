#include <ATLComTime.h>
#include <jni.h>

#include "be_derycke_pieter_com_OleDate.h"

JNIEXPORT void JNICALL Java_be_derycke_pieter_com_OleDate_setDate
	(JNIEnv* env, jobject obj, jdouble jdValue)
{
	jclass cls;
	jmethodID mid;
	COleDateTime date(jdValue);

	cls = env->GetObjectClass(obj);

	//jaar
	mid = env->GetMethodID(cls, "setYear", "(I)V");
	env->CallVoidMethod(obj, mid, date.GetYear());

	//maand
	mid = env->GetMethodID(cls, "setMonth", "(I)V");
	env->CallVoidMethod(obj, mid, date.GetMonth() - 1);

	//dag
	mid = env->GetMethodID(cls, "setDate", "(I)V");
	env->CallVoidMethod(obj, mid, date.GetDay());

	//uren
	mid = env->GetMethodID(cls, "setHours", "(I)V");
	env->CallVoidMethod(obj, mid, date.GetHour());

	//minuten
	mid = env->GetMethodID(cls, "setMinutes", "(I)V");
	env->CallVoidMethod(obj, mid, date.GetMinute());

	//seconden
	mid = env->GetMethodID(cls, "setSeconds", "(I)V");
	env->CallVoidMethod(obj, mid, date.GetSecond());
}

JNIEXPORT jdouble JNICALL Java_be_derycke_pieter_com_OleDate_toDouble
	(JNIEnv* env, jobject obj)
{
	jclass cls;
	jmethodID mid;
	int year, month, day, hour, minute, second;

	cls = env->GetObjectClass(obj);

	//jaar
	mid = env->GetMethodID(cls, "getYear", "()I");
	year = env->CallIntMethod(obj, mid);

	//maand
	mid = env->GetMethodID(cls, "getMonth", "()I");
	month = env->CallIntMethod(obj, mid);

	//dag
	mid = env->GetMethodID(cls, "getDate", "()I");
	day = env->CallIntMethod(obj, mid);

	//uren
	mid = env->GetMethodID(cls, "getHours", "()I");
	hour = env->CallIntMethod(obj, mid);

	//minuten
	mid = env->GetMethodID(cls, "getMinutes", "()I");
	minute = env->CallIntMethod(obj, mid);

	//seconden
	mid = env->GetMethodID(cls, "getSeconds", "()I");
	second = env->CallIntMethod(obj, mid);

	COleDateTime date(year, month, day, hour, minute, second);
	
	return DATE(date);
}