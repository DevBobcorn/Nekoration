import json

colors = ["black","blue","brown","cyan","gray","green","light_blue","light_gray","lime","magenta","orange","pink","purple","red","white","yellow"]

woods = ["dark_oak","spruce","warped","jungle","oak","crimson","acacia","birch"]
wdids = [         0,       2,       3,       4,    7,        9,      10,     14]

halfTimberRecipes = [
    [ "1P1","P0P","1P1" ], # P0
    [ "  1"," 0 ","1  " ], # P1
    [ "1  "," 0 ","  1" ], # P2
    [ "  1"," 0 ","1 1" ], # P3
    [ "1  "," 0 ","1 1" ], # P4
    [ " 1 ","101" ],       # P5
    [ "1 1"," 0 ","1 1" ], # P6
    [ " 1 ","101"," 1 " ], # P7
    [ "111","101","111" ], # P8
    [ " 1 "," 0 "," 1 " ], # P9
]

pillarRecipe = [ "#","#","#" ]
chairRecipe = [ "#  ","###","# #" ]
tableRecipe = [ "###","# #","# #" ]
roundTableRecipe = [ "###"," # "," # " ]

windows = ["simple","cross","lancet","arch","shade"]
windowRecipes = [
    [ "010","1#1","010" ], # Simple
    [ " 1 ","101"," 1 " ], # Cross
    [ " 1 ","101","111" ], # Lancet
    [ "111","101" ],       # Arch
    [ "111","101","111" ]  # Shade
]

awningRecipe = [ "  #"," # ","#  " ]
awningShortRecipe = [ "  #"," # ","# 1" ]

easelMenuRecipe = [ "#0#","#0#","# #" ]

def getVanilla(itemId):
    return { "item": "minecraft:" + itemId }


if True: # Neko Shaped / Vanilla Shaped
    def getHalfTimberResult(style):
        return {
                "item" : "nekoration:half_timber_p" + str(style),
                "count": 1
            }

    def getHalfTimberNBTResult(style, wood):
        return {
                "item" : "nekoration:half_timber_p" + str(style),
                "nbt": {
                    "color_0": wood,
                    "color_1": 14
                },
                "count": 1
            }

    def getBlockResult(block, style, count):
        if (style != ""):
            block += "_" + style
        return {
                "item" : "nekoration:" + block,
                "count": count
            }

    def getBlockNBTResult(block, style, color, count):
        if (style != ""):
            block += "_" + style
        return {
                "item" : "nekoration:" + block,
                "nbt": {
                    "color": color
                },
                "count": count
            }

    with open("template_crafting.txt", "r+") as template:
        recipeObj = json.loads(template.read())
        # Neko Shaped
        # half-timber
        recipeObj['type'] = "nekoration:neko_crafting_shaped"
        recipeObj['key']['1'] = getVanilla("stick")
        recipeObj['key']['0'] = { "item" : "nekoration:half_timber_p0" }
        for style in range(1, len(halfTimberRecipes)):
            recipeObj['pattern'] = halfTimberRecipes[style]
            recipeObj['result'] = getHalfTimberResult(style)
            with open("half_timber/p" + str(style) + ".json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))

        # half-timber base
        recipeObj['key']['P'] = getVanilla("paper")
        recipeObj['pattern'] = halfTimberRecipes[0]
        for wood in range(0, len(woods)):
            recipeObj['key']['0'] = getVanilla(woods[wood] + "_planks")
            recipeObj['result'] = getHalfTimberNBTResult(0, wdids[wood])
            with open("half_timber/" + woods[wood] + "_p0.json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))

        # half-timber pillar
        recipeObj['key'].clear()
        recipeObj['pattern'] = pillarRecipe
        for i in range(0, 3):
            recipeObj['key']['#'] = { "item" : "nekoration:half_timber_p" + str(i) }
            recipeObj['result'] = { "item" : "nekoration:half_timber_pillar_p" + str(i), "count": 3 }
            with open("half_timber/pillar_p" + str(i) + ".json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))

        #window
        recipeObj['key'].clear()
        recipeObj['key']['0'] = { "item" : "nekoration:window_simple" }
        recipeObj['key']['1'] = getVanilla("stick")
        for style in range(1, len(windowRecipes)):
            recipeObj['pattern'] = windowRecipes[style]
            recipeObj['result'] = getBlockResult("window", windows[style], 1)
            with open("window/window_" + windows[style] + ".json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))

        # window base
        recipeObj['pattern'] = windowRecipes[0]
        recipeObj['key']['#'] = getVanilla("glass")
        for wood in range(0, len(woods)):
            recipeObj['key']['0'] = getVanilla(woods[wood] + "_planks")
            recipeObj['result'] = getBlockNBTResult("window", windows[0], wdids[wood], 4)
            with open("window/" + woods[wood] + "_window.json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))

        # colorful awning
        recipeObj['key'].clear()
        recipeObj['pattern'] = awningRecipe
        for col in range(0, len(colors)):
            recipeObj['key']['#'] = getVanilla(colors[col] + "_wool")
            recipeObj['result'] = getBlockNBTResult("awning", "pure", col, 3)
            with open("decor/" + colors[col] + "_awning_pure.json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))
        recipeObj['pattern'] = awningShortRecipe
        recipeObj['key']['1'] = getVanilla("stick")
        for col in range(0, len(colors)):
            recipeObj['key']['#'] = getVanilla(colors[col] + "_wool")
            recipeObj['result'] = getBlockNBTResult("awning", "pure_short", col, 3)
            with open("decor/" + colors[col] + "_awning_pure_short.json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))

        # easel menu
        recipeObj['key'].clear()
        recipeObj['pattern'] = easelMenuRecipe
        for wood in range(0, len(woods)):
            recipeObj['key']['#'] = getVanilla(woods[wood] + "_planks")
            recipeObj['key']['0'] = getVanilla("black_concrete")
            recipeObj['result'] = getBlockNBTResult("easel_menu", "", wdids[wood], 4)
            with open("decor/" + woods[wood] + "_easel_menu.json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))
            recipeObj['key']['0'] = getVanilla("white_concrete")
            recipeObj['result'] = getBlockNBTResult("easel_menu","white", wdids[wood], 4)
            with open("decor/" + woods[wood] + "_easel_menu_white.json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))
        

        # Vanilla Shaped
        recipeObj['type'] = "minecraft:crafting_shaped"
        recipeObj['key'].clear()
        # funiture
        for wood in range(0, len(woods)):
            recipeObj['key']['#'] = getVanilla(woods[wood] + "_planks")
            recipeObj['pattern'] = chairRecipe
            recipeObj['result'] = { "item" : "nekoration:" + woods[wood] + "_chair", "count": 4 }
            with open("furniture/" + woods[wood] + "_chair.json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))
            recipeObj['pattern'] = tableRecipe
            recipeObj['result'] = { "item" : "nekoration:" + woods[wood] + "_table", "count": 4 }
            with open("furniture/" + woods[wood] + "_table.json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))
            recipeObj['pattern'] = roundTableRecipe
            recipeObj['result'] = { "item" : "nekoration:" + woods[wood] + "_round_table", "count": 4 }
            with open("furniture/" + woods[wood] + "_round_table.json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))
            

if False: # Stonecutting
    stones = ["layered","base","base_bottom","frame","frame_bottom","pillar","pillar_bottom","doric","corinthian","ionic"]
    decors = ["window_sill","window_top","window_frame","stone_pot"]

    with open("template_stonecutting.txt", "r+") as template:
        recipeObj = json.loads(template.read())
        # smooth stone -> full stone blocks
        for style in stones:
            recipeObj['result'] = "nekoration:stone_" + style
            recipeObj['ingredient']['item'] = "minecraft:smooth_stone"
            with open("neko_stony/smooth2" + style + ".json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))

        # stone base / stone base bottom -> stone decors
        for decor in decors:
            recipeObj['result'] = "nekoration:" + decor
            recipeObj['ingredient']['item'] = "nekoration:stone_base"
            with open("neko_stony/base2" + decor + ".json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))
            recipeObj['ingredient']['item'] = "nekoration:stone_base_bottom"
            with open("neko_stony/bottom2" + decor + ".json", "w+") as f:
                f.write(json.dumps(recipeObj, sort_keys=False, indent=4, separators=(',', ': ')))















    
        
