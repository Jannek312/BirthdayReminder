package de.mcelements.birthday.reminder.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class BirthdayList {

    private ArrayList<Birthday> birthdays = new ArrayList<>();

    public BirthdayList(Birthday... birthdays) {
        for (Birthday birthday : birthdays) {
            this.birthdays.add(birthday);
        }
    }

    public void add(Birthday... birthdays) {
        for (Birthday birthday : birthdays) {
            this.birthdays.add(birthday);
        }
    }

    public Birthday[] findBirthdays(BirthdayType type, String filterTemp) { //TODO rewrite!
        return findBirthdays(type, filterTemp, -1);
    }

    public Birthday[] findBirthdays(BirthdayType type, String filterTemp, int limit) { //TODO rewrite!
        final String filter = (filterTemp != null) ? filterTemp.toLowerCase() : "";
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        Birthday[] array = null;
        System.out.println("limit: " + limit);
        switch (type) {
            case PAST:
                today.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR) - 1);

                array = birthdays.stream().filter(birthday -> birthday.getCalendar(true).before(today)
                        //limit -1 or in limit
                        && (limit == -1 || birthday.getCalendar(true).getTime().getTime() >= today.getTime().getTime() - (1000 * 60 * 60 * 24 * limit))

                        //filter empty or match
                        && filter(birthday, filter)
                ).sorted(Collections.reverseOrder()).toArray(Birthday[]::new);

                break;
            case TODAY:
                array = birthdays.stream().filter(birthday -> birthday.getCalendar(true).get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

                        //filter empty or match
                        && filter(birthday, filter)
                ).sorted().toArray(Birthday[]::new);

                break;
            case FUTURE:
                array = birthdays.stream().filter(birthday -> birthday.getCalendar(true).after(today)
                        //limit -1 or in limit
                        && (limit == -1 || birthday.getCalendar(true).getTime().getTime() <= today.getTime().getTime() + (1000 * 60 * 60 * 24 * limit))

                        //filter empty or match
                        && filter(birthday, filter)
                ).sorted().toArray(Birthday[]::new);
                break;
        }

        System.out.println();
        System.out.println();
        System.out.println("Sorted arrays:");
        System.out.println(type.name() + ":");
        for (Birthday birthday : array) {
            System.out.println(birthday);
        }
        System.out.println();
        System.out.println();
        return array;
    }

    private boolean filter(final Birthday birthday, final String filter) {
        //filter empty or match
        return filter.trim().isEmpty() || (
                birthday.getName(true).contains(filter.toLowerCase())
                        || birthday.getDate().toString().contains(filter)
                        || birthday.getMail(true).toLowerCase().contains(filter.toLowerCase())
                        || birthday.getPhone(true).toLowerCase().contains(filter.toLowerCase())
                        || birthday.getListText().toLowerCase().contains(filter.toLowerCase()));
    }


    public enum BirthdayType {
        PAST(-10),
        TODAY(0),
        FUTURE(10);

        final private int range;

        BirthdayType(int range) {
            this.range = range;
        }

        public boolean check(Date date) {
            return true;
        }


    }

    public Birthday findBirthdayByName(final String name) {
        return birthdays.stream().filter(birthday -> birthday.getListText().equals(name) ||
                birthday.getListText(true).equals(name)).findFirst().orElse(null);
    }

}
