# How to use paintings in Nekoration

## 1. Get a Palette and a Blank Painting

They can either be found in the Creative Inventory, or be crafted in Survival Mode. With a palette, you can pick up to 6 colors at a time and use them when creating your painting. The size of blank painting items can be changed through a right click in the air.

<span style="display: inline-block;">
   <img src="https://s2.loli.net/2022/08/03/2RZ6CTB5QXepKNE.png" alt="Painting Recipe" width="160" height="90">
   <img src="https://s2.loli.net/2022/08/03/NdDJ9Ep3X6LIeox.png" alt="Palette Recipe" width="160" height="90">
   <img src="https://s2.loli.net/2022/08/03/vMEfgUJwTjh62t5.png" alt="Painting Size" width="160" height="90">
   <img src="https://s2.loli.net/2022/08/03/Zyt5UhumIpaEg8f.png" alt="Palette Color" width="160" height="90">
</span>

## 2. Start to paint!

Then, you can place your blank painting on a wall, and the size of this painting is fixed at this time and cannot be changed anymore. Simply right click on it with a palette in your hand, and you can start painting!

![](https://s2.loli.net/2022/08/03/mQyVMzXEUO4sYou.png)

## 3. Saving/Loading paintings

You can input a path in the textfield at the top of the GUI, specifying the image file you want to save to/load from. The root directory of local paths is `.minecraft/nekopaint`, and these paths should contain the extension name(.jpg or .png). For png images it's still OK to omit the extension name, which means you can simply use "foo" to refer to "foo.png", but for jpg images a full name is required.

*   **Save Painting:** Save the whole painting to an image file, canvas included.
*   **Save Painting Content:** Save only the painting content to an image file without the canvas. This function can be especially useful when you want to move painting content from one painting to another.
*   **Load Local Image Files:** Load an image file under `.minecraft/nekopaint` onto the canvas. A few loading parameters are supported: Target Left Offset, Target Top Offset, Source Left Offset, Source Top Offset, Scale, Width Limit, and Height Limit. Check the image below to see what these parameters do.
*   **Load Images From Web URLs:** The editor can also load images from web URLs, and the above parameters still work in this case.

![Painting Hints](https://s2.loli.net/2022/08/10/lTwZeQpnL7PF1Dk.png)

## 4. Have fun!