/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.kingstar.messaging.api;

public class ErrorInfo {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ErrorInfo(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ErrorInfo obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        KSKingMQAPIJNI.delete_ErrorInfo(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setErrorId(int value) {
    KSKingMQAPIJNI.ErrorInfo_errorId_set(swigCPtr, this, value);
  }

  public int getErrorId() {
    return KSKingMQAPIJNI.ErrorInfo_errorId_get(swigCPtr, this);
  }

  public void setErrorMessage(String value) {
    KSKingMQAPIJNI.ErrorInfo_errorMessage_set(swigCPtr, this, value);
  }

  public String getErrorMessage() {
    return KSKingMQAPIJNI.ErrorInfo_errorMessage_get(swigCPtr, this);
  }

  public ErrorInfo() {
    this(KSKingMQAPIJNI.new_ErrorInfo(), true);
  }

}