/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.kingstar.messaging.api;

public class KSKingMQ {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected KSKingMQ(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(KSKingMQ obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        throw new UnsupportedOperationException("C++ destructor does not have public access");
      }
      swigCPtr = 0;
    }
  }

  protected void swigDirectorDisconnect() {
    swigCMemOwn = false;
    delete();
  }

  public void swigReleaseOwnership() {
    swigCMemOwn = false;
    KSKingMQAPIJNI.KSKingMQ_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    KSKingMQAPIJNI.KSKingMQ_change_ownership(this, swigCPtr, true);
  }

  public static KSKingMQ CreateKingMQ(String config_path) {
    long cPtr = KSKingMQAPIJNI.KSKingMQ_CreateKingMQ__SWIG_0(config_path);
    return (cPtr == 0) ? null : new KSKingMQ(cPtr, false);
  }

  public static KSKingMQ CreateKingMQ() {
    long cPtr = KSKingMQAPIJNI.KSKingMQ_CreateKingMQ__SWIG_1();
    return (cPtr == 0) ? null : new KSKingMQ(cPtr, false);
  }

  public static void DestroyKingMQ(KSKingMQ api) {
    KSKingMQAPIJNI.KSKingMQ_DestroyKingMQ(KSKingMQ.getCPtr(api), api);
  }

  public static String GetApiVersion() {
    return KSKingMQAPIJNI.KSKingMQ_GetApiVersion();
  }

  public APIResult ConnectServer(KSKingMQSPI pSpi) {
    return APIResult.swigToEnum(KSKingMQAPIJNI.KSKingMQ_ConnectServer(swigCPtr, this, KSKingMQSPI.getCPtr(pSpi), pSpi));
  }

  public APIResult ReqSubscribe(ReqSubscribeField pReqSubscription) {
    return APIResult.swigToEnum(KSKingMQAPIJNI.KSKingMQ_ReqSubscribe(swigCPtr, this, ReqSubscribeField.getCPtr(pReqSubscription), pReqSubscription));
  }

  public APIResult publish(String routingKey, byte[] pMsgbuf, int durable) {
    return APIResult.swigToEnum(KSKingMQAPIJNI.KSKingMQ_publish(swigCPtr, this, routingKey, pMsgbuf, durable));
  }

  public KSKingMQ() {
    this(KSKingMQAPIJNI.new_KSKingMQ(), true);
    KSKingMQAPIJNI.KSKingMQ_director_connect(this, swigCPtr, true, true);
  }

}
