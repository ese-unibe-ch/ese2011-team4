package models;

import java.util.ArrayList;
import java.util.List;
 
public class Email
{
 private String from;
 private String[] to;
 private String[] cc;
 private String[] bcc;
 private String subject;
 private String text;
 private String mimeType;
 
 
 
 public String getFrom()
 {
  return from;
 }
 public void setFrom(String from)
 {
  this.from = from;
 }
 public String[] getTo()
 {
  return to;
 }
 public void setTo(String... to)
 {
  this.to = to;
 }
 public String[] getCc()
 {
  return cc;
 }
 public void setCc(String... cc)
 {
  this.cc = cc;
 }
 public String[] getBcc()
 {
  return bcc;
 }
 public void setBcc(String... bcc)
 {
  this.bcc = bcc;
 }
 public String getSubject()
 {
  return subject;
 }
 public void setSubject(String subject)
 {
  this.subject = subject;
 }
 public String getText()
 {
  return text;
 }
 public void setText(String text)
 {
  this.text = text;
 }
 public String getMimeType()
 {
  return mimeType;
 }
 public void setMimeType(String mimeType)
 {
  this.mimeType = mimeType;
 }
 
 
 
}