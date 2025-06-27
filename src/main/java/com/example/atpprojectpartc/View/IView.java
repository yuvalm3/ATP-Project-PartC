// com/example/atpprojectpartc/View/IView.java
package com.example.atpprojectpartc.View;

import com.example.atpprojectpartc.ViewModel.MyViewModel;
import java.util.Observer;

/**
 * IView מאפשר הזרקת ViewModel לכל Controller
 * ומתיר ל־Controller לרשום את עצמו כ־Observer על ה־ViewModel.
 */
public interface IView extends Observer {
    /**
     * נותן ל־Controller גישה ל־ViewModel
     * ושם את עצמו כ־Observer שלו.
     */
    void setViewModel(MyViewModel vm);
}
