package com.stu.minote.http.res;

import com.stu.minote.entity.NoteBeen;

import java.util.List;

public class NoteRes {
   private List<NoteBeen> notes;
   private int total;

   public List<NoteBeen> getNotes() {
      return notes;
   }

   public void setNotes(List<NoteBeen> notes) {
      this.notes = notes;
   }

   public int getTotal() {
      return total;
   }

   public void setTotal(int total) {
      this.total = total;
   }
}
