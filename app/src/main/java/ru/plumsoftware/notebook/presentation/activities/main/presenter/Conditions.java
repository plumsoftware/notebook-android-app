package ru.plumsoftware.notebook.presentation.activities.main.presenter;

public abstract class Conditions {
    public static class All extends Conditions {}
    public static class Search extends Conditions {
        private final String query;
        public Search(String str){
            this.query = str;
        }

        public String getQuery() {
            return query;
        }
    }
}
