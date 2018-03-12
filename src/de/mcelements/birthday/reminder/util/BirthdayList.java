package de.mcelements.birthday.reminder.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class BirthdayList {

    private ArrayList<Birthday> birthdays = new ArrayList<>();

    public BirthdayList(Birthday... birthdays) {
        for (Birthday birthday : birthdays) {
            this.birthdays.add(birthday);
        }
    }

    public void add(Birthday... birthdays){
        for (Birthday birthday : birthdays) {
            this.birthdays.add(birthday);
        }
    }

    public Birthday[] findBirthdaysPast() {
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE),0,0,0);

        return (Birthday[]) birthdays.stream().filter(birthday -> {
            Calendar birthdayC = Calendar.getInstance();
            birthdayC.setTime(birthday.getDate());
            birthdayC.set(Calendar.YEAR, today.get(Calendar.YEAR));
            return birthdayC.after(today); //TODO?
        }).toArray();
    }

    public Birthday[] findBirthdays(BirthdayType type, String filter){
        return birthdays.toArray(new Birthday[birthdays.size()]);
    }

    public Birthday[] findBirthdaysToday() {
        final Calendar current = Calendar.getInstance();
        current.set(current.get(Calendar.YEAR),current.get(Calendar.MONTH),current.get(Calendar.DATE),0,0,0);

        return (Birthday[]) birthdays.stream().filter(birthday -> {
            Calendar c = Calendar.getInstance();
            c.setTime(birthday.getDate());
            return c.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR);
        }).toArray();
    }

    public Birthday[] findBirthdaysFuture() {
        final Calendar current = Calendar.getInstance();
        current.set(current.get(Calendar.YEAR),current.get(Calendar.MONTH),current.get(Calendar.DATE),0,0,0);

        return (Birthday[]) birthdays.stream().filter(birthday -> {
            Calendar c = Calendar.getInstance();
            c.setTime(birthday.getDate());
            c.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE),0,0,0);
            return c.after(current);
        }).toArray();
    }

    public enum  BirthdayType{
        PAST(-10),
        TODAY(0),
        FUTURE(10);

        final private int range;

        BirthdayType(int range){
            this.range = range;
        }

        public boolean check(Date date){
            return true;
        }


    }

}
