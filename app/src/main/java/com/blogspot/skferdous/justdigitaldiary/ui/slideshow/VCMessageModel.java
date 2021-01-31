package com.blogspot.skferdous.justdigitaldiary.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VCMessageModel extends ViewModel {

    private MutableLiveData<String> mText;

    public VCMessageModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}