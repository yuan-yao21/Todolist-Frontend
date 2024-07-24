package com.stu.minote.http.res;

import java.io.Serializable;

public class LoginRes implements Serializable {

   /**
    * jwt : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MTg0MTIwMjMsInVzZXJfaWQiOjE3LCJuaWNrbmFtZSI6IiJ9._jX9LjDeF9tr6hcIbiAD4sOW_xy6h72Ss2mcN7inPP0
    * userId : 17
    * username : 123123
    * nickname :
    */

   private String jwt;
   private String userId;
   private String username;
   private String nickname;
   private String message;

   public String getJwt() {
      return jwt;
   }

   public void setJwt(String jwt) {
      this.jwt = jwt;
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getNickname() {
      return nickname;
   }

   public void setNickname(String nickname) {
      this.nickname = nickname;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }
}
