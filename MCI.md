# What is MCI?
The MCI system, or the **M**od **C**ompatibility **I**nformation system (originally just called the "defines" system) is the system this save editor uses for mod compatibility. It's exactly what it says on the tin.

# How does MCI work?
The MCI system uses JavaScript to define and declare values.
An example for an MCI configuration file can be found [here](src/com/leo/cse/frontend/default.mci).  
Every MCI file should contain the following functions. *Note: CSE always uses double resolution images, take this in consideration when dealing with positions and sizes!*
## Metadata
- `getName:String` - Gets the MCI file's name.
- `getAuthor:String` - Gets the MCI file's author(s).

## Game information
- `getExeName:String` - Gets the mod executable's name.
- `getArmsImageYStart:Number` - Gets the starting Y position for weapon icons in ArmsImage.
- `getArmsImageSize:Number` - Gets the size of weapon icons in ArmsImage.
- `getFPS:Number` - Gets the FPS. Used for calculating the "Seconds Played" field.
- `getGraphicsResolution:Number` - Gets the game's resolution.

## Information arrays
To add an empty space to any of these arrays, add a `null`.
- `getSpecials:Array<String>` - Gets a list of special support features to enable. Valid values are:  
`MimHack` for the <MIM hack,  
`VarHack` for the <VAR hack (cannot be enabled with <MIM or <BUY),  
`PhysVarHack` for the <PHY hack (depends on <VAR),  
`BuyHack` for the <BUY hack.  
If no special support is required, `null` can also be returned.
- `getMapNames:Array<String>` - Gets a list of map names. Only used when an executable isn't loaded.
- `getSongNames:Array<String>` - Gets a list of song names.
- `getEquipNames:Array<String>` - Gets a list of equip names.
- `getWeaponNames:Array<String>` - Gets a list of weapon names.
- `getItemNames:Array<String>` - Gets a list of item names.
- `getWarpNames:Array<String>` - Gets a list of warp names.
- `getWarpLocNames:Array<String>` - Gets a list of warp location names.
- `getSaveFlagID:Number` - Gets the ID of the "game was saved" flag. This flag ID will not be modifiable.
- `getFlagDescriptions:Array<String>` - Gets a list of flag descriptions.

## Player extras
- `getPlayerFrame:java.awt.Rectangle` - Gets the player's frame rectangle.
- `getPlayerOffset:java.awt.Point` - Gets the player's offset in pixels.

Both of these functions get the following parameters:  
- `x:Number` - The X position of the player in pixels.
- `y:Number` - The Y position of the player in pixels.
- `leftright:Boolean` - `false` if the player is facing left, `right` if the player is facing right.
- `costume:Number` - The costume ID the player is currently using. If no costume hacks (<MIM or TSC+) are applied: 0 if the Mimiga Mask is not equipped, 1 if the Mimiga Mask is equipped.

## Entity extras
Both of the following functions receive one parameter: a `WrappedPxeEntry` object.
Every `WrappedPxeEntry` object has the following fields (they're public, so just doing `object.field` is okay):
- `x:Number` - The X position of the entity in tiles.
- `y:Number` - The Y position of the entity in tiles.
- `flagID:Number` - The entity's assigned flag ID.
- `eventNum:Number` - The entity's assigned event number.
- `flags:Array<Boolean>` - The entity's flags.

Here are the functions themselves:
- `getEntityFrame:java.awt.Rectangle` - Gets the entity's frame rectangle.
- `getEntityOffset:java.awt.Point` - Gets the entity's position offset in pixels.
