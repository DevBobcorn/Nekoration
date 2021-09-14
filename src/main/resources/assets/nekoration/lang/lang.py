# This is a Python3 Script used to generate language files for this mod
import json, random, copy, hashlib

colors = ["black","blue","brown","cyan","gray","green","light_blue","light_gray","lime","magenta","orange","pink","purple","red","white","yellow","unknown","blank"]
colors_us  = ["Black","Blue","Brown","Cyan","Gray","Green","Light Blue","Light Gray","Lime","Magenta","Orange","Pink","Purple","Red","White","Yellow","Some","Blank"]
wooden_us  = ["Dark Oak","Blue Wood","Spruce","Warped","Jungle","Green Wood","Magic","Oak","Willow","Crimson","Acacia","Mahogany","Umbran","Cherry","Birch","Palm","Some","Air"]
colors_cn = ["黑色","蓝色","棕色","青色","灰色","绿色","淡蓝色","淡灰色","黄绿色","品红色","橙色","粉色","紫色","红色","白色","黄色","","空白"]
wooden_cn = ["深色橡木","蓝木","云杉木","诡异木","丛林木","绿木","魔法木","橡木","柳木","绯红木","金合欢木","桃花心木","暗影木","樱木","白桦木","棕榈木","","空气"]
woods = ["pumpkin","dark_oak","spruce","warped","jungle","oak","crimson","acacia","birch"]
woods_cn = ["南瓜","深色橡木","云杉木","诡异木","丛林木","橡木","绯红木","金合欢木","白桦木"]
chair_us = "Chair"
table_us = "Table"
round_table_us = "Round WOOD Table"
chair_cn = "桌"
table_cn = "椅"
round_table_cn = "圆桌"
stones = ["stone_base","stone_base_bottom","stone_frame","stone_frame_bottom","stone_pillar","stone_pillar_bottom","stone_doric","stone_ionic","stone_corinthian","stone_layered","stone_pot"]
stones_us = ["Stone Base","Stone Base Bottom","Stone Frame","Stone Frame Bottom","Stone Pillar","Stone Pillar Bottom","Doric Stone Pillar","Ionic Stone Pillar","Corinthian Stone Pillar","Layered Stone","Stone Pot"]
stones_cn = ["石壁","石壁底座","石框","石框底座","石柱","石柱底座","多立克石柱","爱奥尼石柱","科林斯石柱","叠层石壁","石质花盆"]
half_timbers = ["half_timber_p0","half_timber_p1","half_timber_p2","half_timber_p3","half_timber_p4","half_timber_p5","half_timber_p6","half_timber_p7","half_timber_p8","half_timber_p9","half_timber_pillar_p0","half_timber_pillar_p1","half_timber_pillar_p2"]
half_timbers_us = ["%s Half Timber","Slash %s Half Timber","Backslash %s Half Timber","Bi-Slash %s Half Timber","Bi-Backslash %s Half Timber","Center %s Half Timber","Cross %s Half Timber","Diamond %s Half Timber","Checkered %s Half Timber","Double %s Half Timber","%s Half Timber Pillar","Slash %s Half Timber Pillar","Backslash %s Half Timber Pillar"]
half_timbers_cn = ["%s半露木","斜纹%s半露木","反斜纹%s半露木","双斜纹%s半露木","双反斜纹%s半露木","中心%s半露木","十字%s半露木","菱形%s半露木","方格%s半露木","双格%s半露木","%s半露木柱","斜纹%s半露木柱","反斜纹%s半露木柱"]
windows = ["window_simple","window_arch","window_cross","window_lancet","window_shade","window_top","window_sill","window_frame"]
windows_us = ["Simple Window","Arch Window","Cross Window","Lancet Window","Shade Window","Window Top","Window Sill","Window Frame"]
windows_cn = ["简易窗","拱形窗","玻璃窗","尖头窗","百叶窗","窗顶","窗台","窗框"]
doors = ["door_1","door_2","door_3","door_tall_1","door_tall_2","door_tall_3"]
doors_us = ["Quartz Door","Chiseled Quartz Door","Quartz Bricks Door","Tall Quartz Door","Tall Chiseled Quartz Door","Tall Quartz Bricks Door"]
doors_cn = ["石英门","錾制石英门","石英砖门","加高石英门","加高錾制石英门","加高石英砖门"]
decors = ["awning_pure","awning_stripe","awning_pure_short","awning_stripe_short","easel_menu","easel_menu_white"]
decors_us = ["%s Awning","%s Stripe Awning","Short %s Awning","Short %s Stripe Awning","%s Easel Menu","%s White Easel Menu"]
decors_cn = ["%s雨篷","%s条纹雨篷","%s短篷","%s条纹短篷","%s展架","%s白色展架"]
# Find it a bit strange to say "Candle Holder with %s candle", so I just ignore their color
items = ["brochure","paw","paw_up","paw_down","paw_left","paw_right","paw_near","paw_far","paw_15","paw_90","palette","painting.blank","painting.painted","painting.magic","wallpaper"]
items_us = ["Neko Brochure [WIP]","Cat's Paw","Move Up","Move Down","Move Left","Move Right","Move Near","Move Far","Rotate 15 Degrees","Rotate 90 Degrees","Palette","Blank Painting (%sx%s)","Painting (%sx%s)","Linked Painting (%sx%s)","%s Wallpaper"]
items_cn = ["Neko手册[WIP]","猫爪","上移","下移","左移","右移","前移","后移","旋转15度","旋转90度","调色板","空白画(%sx%s)","画(%sx%s)","链接画(%sx%s)","%s墙纸"]

miscs = ["lamp_post_iron","lamp_post_gold","lamp_post_quartz","candle_holder_iron","candle_holder_gold","candle_holder_quartz","flower_basket_iron","flower_basket_gold","flower_basket_quartz","phonograph","custom","prismap_table"]
miscs_us = ["Iron Lamp Post","Gold Lamp Post","Quartz Lamp Post","Iron Candle Holder","Gold Candle Holder","Quartz Candle Holder","Hanging Plants","Hanging Plants","Hanging Plants","Phonograph [WIP]","Custom Block","Prismap Table [WIP]"]
miscs_cn = ["铁灯柱","金灯柱","石英灯柱","铁烛台","金烛台","石英烛台","吊盆植物","吊盆植物","吊盆植物","留声机[WIP]","自定义方块","棱镜台[WIP]"]

tabs = ["stone","wooden","window_n_door","decor","neko_tool"]
tabs_us = ["Stone Blocks","Wooden Blocks","Windows & Doors","Decorations","Neko Tools"]
tabs_cn = ["石质方块","木质方块","门窗","装饰","工具"]

guis = ["button.scroll_up","button.scroll_down","button.enable_all","button.disable_all","button.save_painting","button.save_painting_content","button.load_image","button.clear",
        "message.painting_saved","message.painting_content_saved","message.paint_with_palette","message.painting_load_failed","message.link_expired",
        "message.press_key_color_info","message.color_info","message.press_key_debug_info","message.press_key_color_picker_on","message.press_key_color_picker_off","message.press_key_change_grid","message.painting_size_warning","message.painting_size_warning_help","message.size",
        "button.enable_glow","button.disable_glow"]
guis_us = ["Scroll Up","Scroll Down","Enable All","Disable All","Save Painting","Save Painting Content","Load Image File","Clear",
           "Painting saved as %s","Painting content saved as %s","Edit painting with a palette","Failed to load painting %s","Link expired: Failed to find the original painting.",
           "Press %s to toggle Color Info.","Color: %s R:%s G:%s B:%s","Press %s to view debug info.","Press %s to show color picker.","Press %s to hide color picker.","Press %s to change grid size.","Painting of this size cannot be saved to a single item.","Use a Linked Painting to copy/move.","Size:  %sx%s",
           "Enable Glowing Text", "Disable Glowing Text"]
guis_cn = ["向上","向下","选中所有","清除所有","保存绘画","保存绘画内容","读取图像文件","清除",
           "已将绘画保存至%s","已将绘画内容保存至%s","请使用调色板编辑画作","绘画%s加载失败","链接已失效：无法找到原画作",
           "可按下%s键查看颜色信息","颜色：%s 红：%s 绿：%s 蓝：%s","可按下%s键查看调试信息","可按下%s键显示颜色选择器","可按下%s键隐藏颜色选择器","可按下%s键切换网格尺寸","此大小的绘画信息无法存入单个物品","请使用链接画复制或移动","尺寸:  %sx%s",
           "启用发光文本","禁用发光文本"]

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

def getUpperCap(instr):
    strlst = list(instr)
    toUpper = True
    for char in range(0, len(strlst)):
        if (toUpper and strlst[char] >= 'a' and strlst[char] <= 'z'): # Then to Uppercase
            strlst[char] = chr(ord(strlst[char]) - 32)
        # Reset
        toUpper = False
        if (strlst[char] == ' '):
            toUpper = True
    return "".join(strlst)

def getUpper1st(instr):
    strlst = list(instr)
    if (strlst[0] >= 'a' and strlst[0] <= 'z'): # Then to Uppercase
        strlst[0] = chr(ord(strlst[0]) - 32)
    return "".join(strlst)


# English(United States)  |  en_us.json
obj_us  = {}
# 简体中文(中国)          |  zh_cn.json
obj_cn  = {}
# LOLCAT(Kingdom of Catz) |  lol_us.json
obj_lol = {}

# Color Names...
for col in range(0, len(colors)):
    obj_us['color.nekoration.' + colors[col]] = colors_us[col]
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
    obj_us['block.nekoration.' + woods[fur] + '_round_table'] = round_table_us.replace("WOOD", getEnName(woods[fur]))
    obj_us['block.nekoration.' + woods[fur] + '_chair'] = getEnName(woods[fur]) + ' ' + chair_us
    obj_cn['block.nekoration.' + woods[fur] + '_table'] = woods_cn[fur] + table_cn
    obj_cn['block.nekoration.' + woods[fur] + '_chair'] = woods_cn[fur] + chair_cn
    obj_cn['block.nekoration.' + woods[fur] + '_round_table'] = woods_cn[fur] + round_table_cn

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

# [GUI]
for gui in range(0, len(guis)):
    obj_us['gui.nekoration.' + guis[gui]] = guis_us[gui]
    obj_cn['gui.nekoration.' + guis[gui]] = guis_cn[gui]


# Write these objects into files in JSON format
with open("en_us.json", "w+") as f:
    data = json.dumps(obj_us, sort_keys=True, indent=4, separators=(',', ': '))
    f.write(data)

with open("zh_cn.json", "w+", encoding='utf-8') as f:
    data = json.dumps(obj_cn, ensure_ascii=False, sort_keys=True, indent=4, separators=(',', ': '))
    f.write(data)


# FULL Langeez Suprot 4 wee catz!
# Wii apologize 4 de inconviinnis
colors_lol  = ["Blak","Bloo","Brownish","Syan","Gray","Greenish","Lite Bloo","Lite Gray","Limd","Majenta","Ornge","Pinky","Parple","Redish","Waite","Yello","What","Nuh"]
rplc_1 = {
    'far ': ('fur ', 1.0),
    'bug ': ('<*> ', 1.0),
    'wip': ('<#>', 1.0),
    'sh': ('<$>', 1.0),
    'size': ('hugenezz', 1.0),
    'thanks': ('thx', 1.0),
    'gold': ('shiny',1.0),
    'golden': ('shiny', 1.0),
    'diamond': ('heaven', 1.0),
    'cat\'s': ('mai', 1.0),
    'cat': ('mii', 1.0),
    'tion': ('<?>', 0.8),
    'to ': ('2 ', 1.0),
    'and ': ('& ', 1.0),
    'for': ('4', 1.0),
    'pal': ('paw', 1.0),
    'o': ('oo', 0.2),
    'or': ('ur', 0.8),
    'ar': ('aa', 0.5),
    'er': ('ar', 0.365),
    'ts': ('z', 0.6),
    'ur': ('aa', 0.2),
    'ir': ('ur', 0.78),
    'ru': ('oo', 0.89),
    'ny': ('nee', 0.46),
    'ny': ('ni', 0.26),
    'ty': ('tee', 0.76),
    'ti': ('tee', 0.56),
    'tee': ('ti', 0.36),
    'sy': ('sii', 0.86),
    'the': ('teh', 1.0),
    'teh': ('de', 0.3),
    'eco': ('eko', 0.95),
    'ng ': ('n ', 0.99),
    'ph': ('f', 0.79),
    'rr': ('r', 0.69)
}
grop_1 = ['o','a','u']
rplc_2 = {
    '<*>': 'dog',
    '<#>': 'rip',
    '<?>': 'shun',
    '<$>': 'sh'
}

def toLolCat(instr):
    sha1 = hashlib.sha1()
    sha1.update(instr.encode("utf8"))
    #print('hash ' + str(sha1.hexdigest()))
    misteaksiid = sha1.hexdigest()
    print(instr + ' ' + str(misteaksiid))
    random.seed(misteaksiid)
    instr = instr.lower()
    
    for src, tar in rplc_1.items():
        if (src in instr):
            don = random.random()
            if (don < tar[1]):
                instr = instr.replace(src, tar[0])
                
    strlst = list(instr)
    for char in range(0, len(strlst)):
        if (strlst[char] in grop_1):
            don = random.randint(0, 12)
            if (don < 10):
                pikd = random.randint(0, len(grop_1) - 1)
                strlst[char] = grop_1[pikd]
        if (char > 0 and strlst[char] == 's' and strlst[char - 1] != '%'):
            don = random.randint(0, 10)
            if (don < 7):
                strlst[char] = 'z'
    wip = "".join(strlst)
    for jar, fish in rplc_2.items():
        if jar in wip:
            wip = wip.replace(jar, fish)
    if (random.randint(0, 14) < 10):
        proc = getUpper1st(wip)
    else:
        proc = getUpperCap(wip)
    #print(proc + ' ' + str(misteaksiid))
    return proc

with open("lol_us.json", "w+") as f:
    # 1st maek 1 copee
    obj_lol = copy.deepcopy(obj_us)
    for can, tuna in obj_lol.items():
        obj_lol[can] = toLolCat(tuna)
    for col in range(0, len(colors)):
        obj_lol['color.nekoration.' + colors[col]] = colors_lol[col]
    data = json.dumps(obj_lol, sort_keys=True, indent=4, separators=(',', ': '))
    f.write(data)
    # Sooo long & THX 4 ALL teh fishez

