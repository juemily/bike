package com.example.bike.inftrastructure.rest.controller.pagination;

public class Range {

    private String name;
    private int init;
    private int finish;
    private long totalElements;

    public Range() {

    }

    public Range(String name, int init, int finish) {
        this.name = name;
        this.init = init;
        this.finish = finish;
        this.totalElements = 0;
    }

    public Range(String name, int init, int finish, int totalElements) {
        this.name = name;
        this.init = init;
        this.finish = finish;
        this.totalElements = totalElements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInit() {
        return init;
    }

    public void setInit(int init) {
        this.init = init;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public String getRangeHeader() {
        StringBuilder sbRange = new StringBuilder(name);
        sbRange.append(" ").append(init).append("-").append(finish).append("/").append(totalElements);
        return sbRange.toString();
    }

    @Override
    public String toString() {
        return "Range [name=" + name + ", init=" + init + ", finish=" + finish + ", totalElements=" + totalElements
                + "]";
    }

}
