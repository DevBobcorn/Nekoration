#This is a Python3 Script used to generate language files for this mod
import json

colors = ["black","blue","brown","cyan","gray","green","light_blue","light_gray","lime","magenta","orange","pink","purple","red","white","yellow"]
wooden_us = ["Dark Oak","Blue Wooden","Spruce","Warped","Jungle","Green Wooden","Light Blue Wooden","Oak","Lime Wooden","Crimson","Acacia","Pink Wooden","Purple Wooden","Red Wooden","Birch","Yellow Wooden"]
colors_cn = ["黑色","蓝色","棕色","青色","灰色","绿色","淡蓝色","淡灰色","黄绿色","品红色","橙色","粉色","紫色","红色","白色","黄色"]
wooden_cn = ["深色橡木","蓝木","云杉","诡异","丛林","绿木","淡蓝木","橡木","黄绿木","绯红","金合欢","粉木","紫木","红木","白桦","黄木"]
woods = ["pumpkin","dark_oak","spruce","warped","jungle","oak","crimson","acacia","birch"]
woods_cn = ["南瓜","深色橡木","云杉","诡异","丛林","橡木","绯红","金合欢","白桦"]
chair_us = "Chair"
table_us = "Table"
chair_cn = "桌"
table_cn = "椅"
stones = ["stone_base","stone_base_bottom","stone_frame","stone_frame_bottom","stone_pillar","stone_pillar_bottom","stone_doric","stone_ionic","stone_corinthian","stone_layered","stone_pot"]
stones_us = ["Stone Base","Stone Base Bottom","Stone Frame","Stone Frame Bottom","Stone Pillar","Stone Pillar Bottom","Doric Stone Pillar","Ionic Stone Pillar","Corinthian Stone Pillar","Layered Stone","Stone Pot"]
stones_cn = ["石壁","石壁底座","石框","石框底座","石柱","石柱底座","多立克石柱","爱奥尼石柱","科林斯石柱","叠层石壁","石质花盆"]
half_timbers = ["half_timber_p0","half_timber_p1","half_timber_p2","half_timber_p3","half_timber_p4","half_timber_p5","half_timber_p6","half_timber_p7","half_timber_p8","half_timber_p9","half_timber_pillar_p0","half_timber_pillar_p1","half_timber_pillar_p2"]
half_timbers_us = ["%s Half Timber","Slash %s Half Timber","Backslash %s Half Timber","Bi-Slash %s Half Timber","Bi-Backslash %s Half Timber","Center %s Half Timber","Cross %s Half Timber","Diamond %s Half Timber","Checkered %s Half Timber","Double %s Half Timber","%s Half Timber Pillar","Slash %s Half Timber Pillar","Backslash %s Half Timber Pillar"]
half_timbers_cn = ["%s半露木","斜纹%s半露木","反斜纹%s半露木","双斜纹%s半露木","双反斜纹%s半露木","中心%s半露木","十字%s半露木","菱形%s半露木","方格%s半露木","双格%s半露木","%s半露木柱","斜纹%s半露木柱","反斜纹%s半露木柱"]
windows = ["window_arch","window_cross","window_lancet","window_shade","window_top","window_sill"]
windows_us = ["Arch Window","Plain Window","Lancet Window","Shade Window","Window Top","Window Sill"]
windows_cn = ["拱形窗","玻璃窗","尖头窗","百叶窗","窗顶","窗台"]
doors = ["door_1","door_2","door_3","door_tall_1","door_tall_2","door_tall_3"]
doors_us = ["Quartz Door","Chiseled Quartz Door","Quartz Brick Door","Tall Quartz Door","Tall Chiseled Quartz Door","Tall Quartz Brick Door"]
doors_cn = ["石英门","錾制石英门","石英砖门","加高石英门","加高錾制石英门","加高石英砖门"]
decors = ["awning_pure","awning_stripe","awning_pure_short","awning_stripe_short","easel_menu","flower_basket_iron"]
decors_us = ["%s Awning","%s Stripe Awning","Short %s Awning","Short %s Stripe Awning","%s Easel Menu","Hanging Plants [WIP]"]
decors_cn = ["%s雨篷","%s条纹雨篷","%s短篷","%s条纹短篷","%s展架","吊盆植物[WIP]"]
# Find it a bit strange to say "Candle Holder with XXX candle", so I just ignore their color
items = ["paw","paw_up","paw_down","paw_left","paw_right","paw_near","paw_far","paw_15","paw_90","palette", "painting"]
items_us = ["Cat's Paw","Move Up","Move Down","Move Left","Move Right","Move Near","Move Far","Rotate 15 Degrees","Rotate 90 Degrees","Palette","Painting (%sx%s) [WIP]"]
items_cn = ["猫爪","上移","下移","左移","右移","前移","后移","旋转15度","旋转90度","调色板","画(%sx%s)[WIP]"]

miscs = ["lamp_post_iron","lamp_post_gold","lamp_post_quartz","candle_holder_iron","candle_holder_gold","candle_holder_quartz","phonograph","custom"]
miscs_us = ["Iron Lamp Post","Gold Lamp Post","Quartz Lamp Post","Iron Candle Holder","Gold Candle Holder","Quartz Candle Holder","Phonograph [WIP]","Custom Block"]
miscs_cn = ["铁灯柱","金灯柱","石英灯柱","铁烛台","金烛台","石英烛台","留声机[WIP]","自定义方块"]

tabs = ["stone","wooden","window_n_door","furniture","decor","tool"]
tabs_us = ["Stone Blocks","Wooden Blocks","Windows & Doors","Furnitures","Small Decors","Tools"]
tabs_cn = ["石质方块","木质方块","门窗","家具","装饰","工具"]

buttons = ["scroll_up","scroll_down","enable_all","disable_all"]
buttons_cn = ["向上","向下","选中所有","清除所有"]

def getEnNameMine(instr):
    strlst = list(instr)
    toUpper = True
    for char in range(0, len(strlst)):
        if (toUpper and strlst[char] >= 'a' and strlst[char] <= 'z'): # Then to Uppercase
            strlst[char] = chr(ord(strlst[char]) - 32)
        # Reset
        toUpper = False
        if (strlst[char] == '_'):
            toUpper = True
    return "".join(strlst).replace('_', ' ')

def getEnName(instr):
    return instr.replace('_', ' ').title()

# English(United States) |  en_us.json
obj_us = {}
# 简体中文(中国)         |  zh_cn.json
obj_cn = {}


# Color Names...
for col in range(0, len(colors)):
    obj_us['color.nekoration.' + colors[col]] = getEnName(colors[col])
    obj_us['color.wooden.' + colors[col]] = wooden_us[col]
    obj_cn['color.nekoration.' + colors[col]] = colors_cn[col]
    obj_cn['color.wooden.' + colors[col]] = wooden_cn[col]

# [STONE TAB]
for stn in range(0, len(stones)):
    ## Include color translation, note if a space should follow (neko_color)
    obj_us['block.nekoration.' + stones[stn]] = "%s " + stones_us[stn]
    obj_cn['block.nekoration.' + stones[stn]] = "%s" + stones_cn[stn]

# [WOODEN TAB]
for h_t in range(0, len(half_timbers)):
    # (wooden_color, neko_color)
    obj_us['block.nekoration.' + half_timbers[h_t]] = "%s " + half_timbers_us[h_t]
    obj_cn['block.nekoration.' + half_timbers[h_t]] = "%s" + half_timbers_cn[h_t]

# [WINDOW TAB]
for win in range(0, len(windows)):
    # (wooden_color / nekocolor)
    obj_us['block.nekoration.' + windows[win]] = "%s " + windows_us[win]
    obj_cn['block.nekoration.' + windows[win]] = "%s" + windows_cn[win]

obj_us['block.nekoration.window_plant'] = "%s Flowering Window Plant"
obj_cn['block.nekoration.window_plant'] = "开%s花的窗边植物"

# [DOOR TAB]
for dor in range(0, len(doors)):
    # (nekocolor)
    obj_us['block.nekoration.' + doors[dor]] = doors_us[dor]
    obj_cn['block.nekoration.' + doors[dor]] = doors_cn[dor]

# [FURNITURE TAB]
for fur in range(0, len(woods)):
    obj_us['block.nekoration.' + woods[fur] + '_table'] = getEnName(woods[fur]) + ' ' + table_us
    obj_us['block.nekoration.' + woods[fur] + '_chair'] = getEnName(woods[fur]) + ' ' + chair_us
    obj_cn['block.nekoration.' + woods[fur] + '_table'] = woods_cn[fur] + table_cn
    obj_cn['block.nekoration.' + woods[fur] + '_chair'] = woods_cn[fur] + chair_cn

# [DECOR TAB]
for dec in range(0, len(decors)):
    # Color texts Included Already (neko_color / wooden_color)
    obj_us['block.nekoration.' + decors[dec]] = decors_us[dec]
    obj_cn['block.nekoration.' + decors[dec]] = decors_cn[dec]

# [MISC TAB]
for msc in range(0, len(miscs)):
    # Color texts Included Already (neko_color / wooden_color)
    obj_us['block.nekoration.' + miscs[msc]] = miscs_us[msc]
    obj_cn['block.nekoration.' + miscs[msc]] = miscs_cn[msc]

# [ITEM TAB]
for itm in range(0, len(items)):
    obj_us['item.nekoration.' + items[itm]] = items_us[itm]
    obj_cn['item.nekoration.' + items[itm]] = items_cn[itm]

# [TAB NAME]
for tab in range(0, len(tabs)):
    obj_us['itemGroup.' + tabs[tab]] = tabs_us[tab]
    obj_cn['itemGroup.' + tabs[tab]] = tabs_cn[tab]

# [BUTTON]
for btn in range(0, len(buttons)):
    obj_us['gui.button.nekoration.' + buttons[btn]] = getEnName(buttons[btn])
    obj_cn['gui.button.nekoration.' + buttons[btn]] = buttons_cn[btn]
    
# ...

# Write these objects into files in JSON format
with open("en_us.json", "w+") as f:
    data = json.dumps(obj_us, sort_keys=True, indent=4, separators=(',', ': '))
    f.write(data)

with open("zh_cn.json", "w+", encoding='utf-8') as f:
    data = json.dumps(obj_cn, ensure_ascii=False, sort_keys=True, indent=4, separators=(',', ': '))
    f.write(data)
