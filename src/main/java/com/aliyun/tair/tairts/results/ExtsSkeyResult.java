package com.aliyun.tair.tairts.results;

import java.util.ArrayList;
import java.util.List;

public class ExtsSkeyResult {
    private String skey;
    private ArrayList<ExtsLabelResult> labels = new ArrayList<ExtsLabelResult>();
    private ArrayList<ExtsDataPointResult> dataPoints = new ArrayList<ExtsDataPointResult>();

    public ExtsSkeyResult(String skey, List labels, List dataPoints) {
        this.skey = skey;

        int labelsNum = labels.size();
        for (int i = 0; i < labelsNum; i++) {
            List subl = (List) labels.get(i);
            this.labels.add(new ExtsLabelResult(new String((byte[]) subl.get(0)), new String((byte[]) subl.get(1))));
        }

        int dataPointsNum = dataPoints.size();
        for (int i = 0; i < dataPointsNum; i++) {
            List subl = (List) dataPoints.get(i);
            this.dataPoints.add(new ExtsDataPointResult(((Number) subl.get(0)).longValue(), new String((byte[]) subl.get(1))));
        }
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public ArrayList<ExtsLabelResult> getLabels() {
        return labels;
    }

    public ArrayList<ExtsDataPointResult> getDataPoints() {
        return dataPoints;
    }


}