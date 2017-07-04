# CaveSaveEdit
A Java-based Cave Story profile editor, complete with mod support.
# How to Compile
First off, CaveSaveEdit will only compile on Java 8 without modifications to the map sorting functions in FrontUtils and a few other places where lambdas are used.
Since there are no external libraries or resources, compiling CSE is super easy!
There is only one `main` method, located in `com.leo.cse.frontend.Main`.
This is an Eclipse project, although I believe IntelliJ can import Eclipse projects natively.
# How to Use
Click on "Load profile" in the toolbar to load a profile. If a Profile.dat file is found in the same directory as the application, it will be loaded automatically on startup.  
Modify the values to your liking, and then click on "Save" in the toolbar to save the new profile.  
Yes, it's that simple!
# MCI
The MCI system is used for mod support.  
This system allows users to add custom items, weapons, equipment and more to the save editor with ease.  
See [the MCI readme](MCI.md) for more information about the MCI system.
# To-do
The to-do list for CaveSaveEdit can be found [here](TODO.md).
# Credits
- **Noxid ([@taedixon](https://github.com/taedixon)):** Executable related code ([classes in the "backend.exe" package](src/com/leo/cse/backend/exe)), stolen from **[Booster's Lab](https://github.com/taedixon/boosters-lab)**.
- **zxin ([@zxinmine](https://github.com/zxinmine)):** UI sprites ([ui.png](src/com/leo/cse/frontend/ui.png) and [shadow.png](src/com/leo/cse/frontend/shadow.png))
- **Carrotlord:** StrTools class ([com.carrotlord.string.StrTools](src/com/carrotlord/string/StrTools.java))
