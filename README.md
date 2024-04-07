This Android app utilizes a WebView to display Google's homepage. Additionally, it includes a feature where after a user performs a specific action (pressing a button 10 times), a PIN verification dialog is displayed. If the correct PIN is entered (1234), the app is unlocked. 

Moreover, the app ensures accessibility permissions are granted and verifies if it's set as the default launcher. If not, it prompts the user to set it as the default launcher or grant accessibility permissions. 

In the background, there's an AccessibilityService (`MotionClass`) monitoring events. If the app isn't unlocked and the user navigates away from it, the service redirects them back to the app's main screen.

CLASSES:

- **MainActivity**: 
    - This class represents the main activity of the app.
    - The purpose of this app is to display a WebView that loads the Google homepage.
    - It includes functionality to show a dialog for entering a PIN after a certain number of button clicks, unlocking the app if the correct PIN is entered.
    - Additionally, it contains logic to manage fullscreen mode, check accessibility service permissions, and ensure that the app is set as the default launcher.
- **MotionClass**:
    - This class extends AccessibilityService to handle accessibility events.
    - It intercepts accessibility events and redirects the user to the home screen if the app is not unlocked with the correct PIN.
    - This class works in conjunction with MainActivity to ensure that the user cannot navigate away from the app without unlocking it.
