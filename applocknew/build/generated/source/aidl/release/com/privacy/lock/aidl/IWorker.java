/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\Gitlab\\Applock-aurora-v1\\applocknew\\src\\main\\aidl\\com\\privacy\\lock\\aidl\\IWorker.aidl
 */
package com.privacy.lock.aidl;
/**
 * Created by superjoy on 2014/8/26.
 */
public interface IWorker extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.privacy.lock.aidl.IWorker
{
private static final java.lang.String DESCRIPTOR = "com.privacy.lock.aidl.IWorker";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.privacy.lock.aidl.IWorker interface,
 * generating a proxy if needed.
 */
public static com.privacy.lock.aidl.IWorker asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.privacy.lock.aidl.IWorker))) {
return ((com.privacy.lock.aidl.IWorker)iin);
}
return new com.privacy.lock.aidl.IWorker.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_notifyApplockUpdate:
{
data.enforceInterface(DESCRIPTOR);
this.notifyApplockUpdate();
reply.writeNoException();
return true;
}
case TRANSACTION_toggleProtectStatus:
{
data.enforceInterface(DESCRIPTOR);
this.toggleProtectStatus();
reply.writeNoException();
return true;
}
case TRANSACTION_updateProtectStatus:
{
data.enforceInterface(DESCRIPTOR);
this.updateProtectStatus();
reply.writeNoException();
return true;
}
case TRANSACTION_showNotification:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.showNotification(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unlockApp:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.unlockApp(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unlockLastApp:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.unlockLastApp(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_homeDisplayed:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.homeDisplayed();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_notifyShowWidget:
{
data.enforceInterface(DESCRIPTOR);
this.notifyShowWidget();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.privacy.lock.aidl.IWorker
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void notifyApplockUpdate() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notifyApplockUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void toggleProtectStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_toggleProtectStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateProtectStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_updateProtectStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void showNotification(boolean yes) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((yes)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_showNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean unlockApp(java.lang.String pkg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pkg);
mRemote.transact(Stub.TRANSACTION_unlockApp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean unlockLastApp(boolean unlockAlways) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((unlockAlways)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_unlockLastApp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean homeDisplayed() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_homeDisplayed, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void notifyShowWidget() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notifyShowWidget, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_notifyApplockUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_toggleProtectStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_updateProtectStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_showNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_unlockApp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_unlockLastApp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_homeDisplayed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_notifyShowWidget = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
}
public void notifyApplockUpdate() throws android.os.RemoteException;
public void toggleProtectStatus() throws android.os.RemoteException;
public void updateProtectStatus() throws android.os.RemoteException;
public void showNotification(boolean yes) throws android.os.RemoteException;
public boolean unlockApp(java.lang.String pkg) throws android.os.RemoteException;
public boolean unlockLastApp(boolean unlockAlways) throws android.os.RemoteException;
public boolean homeDisplayed() throws android.os.RemoteException;
public void notifyShowWidget() throws android.os.RemoteException;
}
