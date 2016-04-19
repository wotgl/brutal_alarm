package nazvaniye.nado.brutalalarm;


import com.orm.SugarRecord;

import java.util.ArrayList;

public class Data {
    public static class AlarmTable extends SugarRecord {
        public Boolean enabled;
        public Integer hour;
        public Integer minute;
        public Integer difficulty;
        private String daysOfWeek;

        public AlarmTable() {

        }

        public AlarmTable(Boolean enabled, Integer hour, Integer minute,  ArrayList<Integer> daysOfWeek, Integer difficulty) {
            this.enabled = enabled;
            this.hour = hour;
            this.minute = minute;
            this.difficulty = difficulty;

            setDaysOfWeek(daysOfWeek);
        }

        public ArrayList<Integer> getDaysOfWeek() {
            String[] spliced = this.daysOfWeek.split("\\s+");
            ArrayList<Integer> ret = new ArrayList<>();
            try {
                for (String s : spliced) {
                    ret.add(Integer.valueOf(s));
                }
            } catch (NumberFormatException ignore) {

            }
            return ret;
        }

        public void setDaysOfWeek(ArrayList<Integer> daysOfWeek) {
            this.daysOfWeek = "";
            for(int i = 0; i < daysOfWeek.size(); ++i) {
                this.daysOfWeek += daysOfWeek.get(i) + " ";
            }
        }
    }
}
