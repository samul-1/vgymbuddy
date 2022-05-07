package it.bsamu.sam.virtualgymbuddy.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TestViewModel extends ViewModel {

    private MutableLiveData<Long> testId;

    public MutableLiveData<Long> getTestId() {
        System.out.println("ACCESSING getter");
        if(testId == null) {
            testId = new MutableLiveData<>(10L);
        }
        return testId;
    }

    public void setTestId(long value) {
        System.out.println("setting " + value);
        testId.postValue(value);
    }

}
