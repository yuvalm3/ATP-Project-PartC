// com/example/atpprojectpartc/View/IView.java
package com.example.atpprojectpartc.View;

import com.example.atpprojectpartc.ViewModel.MyViewModel;
import java.util.Observer;

/**
 Interface that defines the contract for connecting a View (Controller) to its ViewModel
 in the MVVM architecture.
 It allows the View to observe updates from the ViewModel and interact with it in a decoupled way.
 */
public interface IView extends Observer {

    void setViewModel(MyViewModel vm);
}
