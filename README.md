# NumberPickerView
Custom Android View to provide a user friendly way of picking numbers. 🧪
# Features
* Set an allowed range for users to pick.
* Provide gestures such as scrolling and long press for a better UX. 
* Give haptic feedback on gestures.
* More stuff I'm planning for the future.. 📅
# Sample app
* ![Sample](/screenshots/sample.gif)
* You can [download](https://drive.google.com/file/d/1EQ2XCTMq4DZGADPcKCmGNsieeuVwA0c-/view?usp=sharing) and try out the sample app yourself too!
# Usage
## Get the AAR
* See [releases](https://github.com/Re1r0/NumberPickerView/releases)
## Add the view to your layout
```
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.mirkamalg.numberpickerview.NumberPickerView
            android:id="@+id/numberPickerView"
            android:layout_width="0dp"
            android:layout_height="60dp"
            app:enableLongPressToReset="true"
            app:enableSwipeGesture="true"
            app:enableUserInput="true"
            app:enableVibration="true"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintDimensionRatio="3.5:1"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="0.05"
            app:maxValue="150"
            app:minValue="0"
            app:swipeGestureSensitivity="medium" />
</androidx.constraintlayout.widget.ConstraintLayout>
```
## Listen for changes
```
numberPickerView.setOnNumberChangedListener(object : OnNumberChangedListener {
            override fun onChanged(newNumber: Int) {
                Log.e("NumberPickerView", "New number: $newNumber")
            }

        })
```
# License
[GNU Lesser General Public License v3.0](https://choosealicense.com/licenses/lgpl-3.0/)
