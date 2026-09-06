![Poster](https://media.forgecdn.net/attachments/384/138/poster.png)

## <strong>Nekoration </strong> <img src="https://s2.loli.net/2022/08/03/qBVTAGy6JPKFxEl.png" width="32px">

## <span style="color: #cc99ff;">A Decoration Mod for Minecraft</span>

## <span style="color: #ff6666;">Note</span>

The development of Nekoration remained halted in the past few years, and there has not been new official releases for Minecraft 1.20 and later during the time period.

In the past few months, Nekoration has undergone a complete redesign and rewrite. The upgraded version will be known as v2, with revamped models and textures, new QoL features and more. Releases for NeoForge 1.21.1 are expected to be available in the upcoming months, and Forge 1.20.1 backport will soon follow.

Here's a quick lookup table for you to check which version you can use for your game:

| Minecraft Version |Nekoration Version                                                             |
| ----------------- |------------------------------------------------------------------------------ |
| 1.16.5-1.19.2     |v1 for Forge                                                                   |
| 1.20.1            |<a href="https://www.curseforge.com/minecraft/mc-mods/nekoration-reborn" target="_blank" rel="noopener noreferrer">v1 unofficial port for Forge by Flechazo098</a>;<br>v2 Forge official backport planned |
| 1.21.1            |Upcoming v2 for NeoForge, with built-in data upgrader                                   |

<div style="padding: 15px; border-left: 5px solid #d9534f; background-color: #fdf7f7; color: #b94a48; border-radius: 4px; margin: 10px 0;">
  ALWAYS backup your save before opening it in a new game instance!
</div>

Since v2 is a complete rewrite from scratch, many ids and data values have changed from the previous version. You can see <a href="https://github.com/DevBobcorn/Nekoration/blob/main/docs/Upgrade.md" target="_blank" rel="noopener noreferrer">here</a> for a technical breakdown of what has changed. A world data upgrader is implemented in Nekoration v2 to handle to data change automatically and you can just load in your old save with v1 mod and continue building. Do remember to backup your old save, just in case.

## <span style="color: #ff6600;">Introduction</span>  
Nekoration is a <span style="color: #ff6600;">NeoForge</span> mod for Minecraft which adds lots of decorational stuff to the game, allowing players to have a wider choice when building &amp; creating.

## <span style="color: #ff9900;">About Its Name</span>  
The name 'Nekoration' is a compound of 'cat' in Japanese(ねこ, neko) and 'decoration', suggesting that the mod is both ornamental and cats-friendly…&nbsp;&nbsp;^._.^= ∫

## <span style="color: #ffcc00;">Features</span>

### Dyeing
Some of Nekoration's blocks are dyeable using vanilla dyes, so that you can turn almost everything in this mod to your favorite tint!

### Connected Models and Textures
Many blocks in Nekoration supports both model and texture connecting, to name a few, Half-Timber Pillars and Lamp Posts. When being placed by players, they'll automatically adapt to visually connect to adjacent blocks.

### Custom Paintings
Painting is a feature with which you can draw your OWN paintings! You can pick colors with a palette item, and enjoy creating your art work on a custom painting! (Click the button below to view tutorial)

<div class="spoiler">
    <p><strong>How to use paintings in Nekoration</strong></p>
    <p>Step 1. Get a Palette and a Blank Painting</p>
    <p>They can either be found in the Creative Inventory, or be crafted in Survival Mode. With a palette, you can pick
        up to 6 colors at a time and use them when creating your painting. The size of blank painting items can be
        changed through a right click in the air.</p>
    <p><span style="display: inline-block;"><img
                src="https://s2.loli.net/2022/08/03/2RZ6CTB5QXepKNE.png" alt="Painting Recipe" width="160" height="90">
            <img src="https://s2.loli.net/2022/08/03/NdDJ9Ep3X6LIeox.png" alt="Palette Recipe" width="160" height="90">
            <img src="https://s2.loli.net/2022/08/03/vMEfgUJwTjh62t5.png" alt="Painting Size" width="160" height="90">
            <img src="https://s2.loli.net/2022/08/03/Zyt5UhumIpaEg8f.png" alt="Palette Color" width="160"
                height="90"></span></p>
    <p>Step 2. Start to paint!</p>
    <p>Then, you can place your blank painting on a wall, and the size of this painting is fixed at this time and cannot
        be changed anymore. Simply right click on it with a palette in your hand, and you can start painting!</p>
    <p><img src="https://s2.loli.net/2022/08/03/mQyVMzXEUO4sYou.png" alt="" width="380" height="214"></p>
    <p>Step 3. Saving/Loading paintings</p>
    <p>You can input a path in the textfield at the top of the GUI, specifying the image file you want to save to/load
        from. The root directory of local paths is <code>.minecraft/nekopaint</code>, and these paths should contain the
        extension name(.jpg or .png).&nbsp;For png images it's still OK to omit the extension name, which means you can
        simply use "foo" to refer to "foo.png", but for jpg images a full name is required.</p>
    <ul>
        <li><strong>Save Painting:</strong> Save the whole painting to an image file, canvas included.</li>
        <li><strong>Save Painting Content:</strong> Save only the painting content to an image file without the canvas.
            This function can be especially useful when you want to move painting content from one painting to another.
        </li>
        <li><strong>Load Local Image Files:</strong> Load an image file under <code>.minecraft/nekopaint</code> onto the
            canvas. A few loading parameters are supported: Target Left Offset, Target Top Offset, Source Left Offset,
            Source Top Offset, Scale, Width Limit, and Height Limit, these parameters should be seperated with '&gt;',
            following the file path. Not all parameters need to be specified, but they <span
                style="color: #ff0000;">must </span>be given <span
                style="color: #ff9900;">in the above order</span>.</li>
        <li><strong>Load Images From Web URLs:</strong>&nbsp;The editor can also load images from web URLs, and the
            above parameters still work in this case.</li>
        <li><strong>A Few Examples:</strong> Assuming that you have an image named <code>foo.png</code> under
            <code>.minecraft\nekopaint</code> folder, inputing <code>foo.png &gt; 0 &gt; 0 &gt; 0 &gt; 0 &gt; 0.5</code>
            will load the image at half size, and using
            <code>foo.png &gt; 10 &gt; 20 &gt; 50 &gt; 60 &gt; 1.0 &gt; 10 &gt; 20</code> will load the part of the
            image from (50, 60) to (60, 80) at position (10, 20) onto your canvas. For another example, you can use
            <code>https://www.misaka-cloud.net/image/Meow.png &gt; 0 &gt; 0 &gt; 0 &gt; 0 &gt; 0.5</code> to load that
            image from a remote server onto your canvas at half size. (That URL doesn't actually exist, just to explain
            how it works)</li>
    </ul>
    <p><span style="display: inline-block;"><img
                src="https://s2.loli.net/2022/08/10/lTwZeQpnL7PF1Dk.png" alt="Painting Hints" width="380"
                height="214"></span><span style="font-size: 18px; display: inline-block;"><img
                src="https://s2.loli.net/2022/08/03/NhlcCy9a5zs1qWQ.png" alt="Have Fun!" width="380"
                height="214"></span></p>
</div>

### Wallpaper
With Nekoration, you can turn any Banner into Wallpaper with exactly the same pattern, just the way you make Shields from Banners. Then you can place it on any wall surface(several pieces of wallpaper can fit into a 1x1x1 space, so don't worry about room corners).

### Neko Brochure
Once you install the mod with Patchouli, you'll find a red brochure in 'Neko Tools' tab, which introduces all aspects of Nekoration, like recipes and mechanics. The book is still being written, and will be of more use as the mod updates.

## <span style="color: #99cc00;">Code Repository</span>  
This project is open-source at  [https://github.com/DevBobcorn/nekoration](https://github.com/DevBobcorn/nekoration "Code Repo"), where you can view the code or contribute to the mod.  

## <span style="color: #00ccff;">Your feedback and suggestions are welcome!</span>
