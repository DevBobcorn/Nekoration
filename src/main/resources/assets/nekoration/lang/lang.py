# This is a Python3 Script used to generate language files for this mod
import json, random, copy, hashlib
import os

color_ids = ["black","blue","brown","cyan","gray","green","light_blue","light_gray","lime","magenta","orange","pink","purple","red","white","yellow","unknown","blank"]
colors_us  = ["Black","Blue","Brown","Cyan","Gray","Green","Light Blue","Light Gray","Lime","Magenta","Orange","Pink","Purple","Red","White","Yellow","Some","Blank"]
colors_cn = ["黑色","蓝色","棕色","青色","灰色","绿色","淡蓝色","淡灰色","黄绿色","品红色","橙色","粉色","紫色","红色","白色","黄色","","空白"]

wood_ids = ["oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo", "crimson", "warped"]
woods_us = ["Oak", "Spruce", "Birch", "Jungle", "Acacia", "Dark Oak", "Mangrove", "Cherry", "Bamboo", "Crimson", "Warped"]
woods_cn = ["橡木", "云杉木", "白桦木", "丛林木", "金合欢木", "深色橡木", "红树木", "樱花木", "竹", "绯红木", "诡异木"]

half_timber_ids = ["p0", "p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9"]
half_timbers_us = ["{wood} Half-Timber", "Bend Sinister {wood} Half-Timber", "Bend {wood} Half-Timber", "Double Bend Sinister {wood} Half-Timber", "Double Bend {wood} Half-Timber", "Center {wood} Half-Timber", "Roundel {wood} Half-Timber", "Saltire {wood} Half-Timber", "Pale {wood} Half-Timber", "Double {wood} Half-Timber"]
half_timbers_cn = ["{wood}半露木", "左斜条{wood}半露木", "右斜条{wood}半露木", "双左斜条{wood}半露木", "双右斜条{wood}半露木", "中心{wood}半露木", "圆形条{wood}半露木", "斜十字条{wood}半露木", "中竖条{wood}半露木", "双格{wood}半露木"]

window_ids = ["simple","arch","cross","lancet","shade"]
windows_us = ["{wood} Simple Window","{wood} Arch Window","{wood} Cross Window","{wood} Lancet Window","{wood} Shade Window"]
windows_cn = ["{wood}简易窗","{wood}拱形窗","{wood}十字窗","{wood}尖头窗","{wood}百叶窗"]

furniture_ids = ["easel_menu","table","chair","cabinet","drawer","drawer_chest","cupboard","wall_shelf"]
furniture_us = ["%s {wood} Easel Menu","{wood} Table","{wood} Chair","{wood} Cabinet","{wood} Drawer","{wood} Chest of Drawers","{wood} Cupboard","{wood} Wall Shelf"]
furniture_cn = ["%s{wood}展架","{wood}桌子","{wood}椅子","{wood}储物柜","{wood}抽屉","{wood}斗柜","{wood}橱柜","{wood}墙架"]

stone_ids = ["granite", "diorite", "andesite"]
stones_us = ["Granite", "Diorite", "Andesite"]
stones_cn = ["花岗岩", "闪长岩", "安山岩"]

stone_has_smooth_variant = [True, True, True]

smooth_stone_block_ids = ["smooth_{stone}", "smooth_{stone}_slab", "smooth_{stone}_stairs"]
smooth_stone_blocks_us = ["Smooth {stone}", "Smooth {stone} Slab", "Smooth {stone} Stairs"]
smooth_stone_blocks_cn = ["平滑{stone}", "平滑{stone}台阶", "平滑{stone}楼梯"]

stone_block_ids = ["polished_smooth_{stone}", "polished_smooth_{stone}_slab", "polished_smooth_{stone}_stairs", "chiseled_{stone}", "{stone}_pot"]
stone_blocks_us = ["Polished Smooth {stone}", "Polished Smooth {stone} Slab", "Polished Smooth {stone} Stairs", "Chiseled {stone}", "{stone} Pot"]
stone_blocks_cn = ["磨制平滑{stone}", "磨制平滑{stone}台阶", "磨制平滑{stone}楼梯", "錾制{stone}", "{stone}花盆"]

door_ids = ["quartz_door","chiseled_quartz_door","quartz_bricks_door","tall_quartz_door","tall_chiseled_quartz_door","tall_quartz_bricks_door"]
doors_us = ["Quartz Door","Chiseled Quartz Door","Quartz Bricks Door","Tall Quartz Door","Tall Chiseled Quartz Door","Tall Quartz Bricks Door"]
doors_cn = ["石英门","錾制石英门","石英砖门","加高石英门","加高錾制石英门","加高石英砖门"]

decor_ids = ["awning_pure","awning_stripe","awning_pure_short","awning_stripe_short","window_plant"]
decors_us = ["%s Awning","%s Stripe Awning","Short %s Awning","Short %s Stripe Awning","%s Flowering Window Plant"]
decors_cn = ["%s雨篷","%s条纹雨篷","%s短篷","%s条纹短篷","开%s花的窗边植物"]

item_ids = ["brochure","paw","paw_up","paw_down","paw_left","paw_right","paw_near","paw_far","paw_15","paw_90","palette","painting.blank","painting.painted","painting.magic","wallpaper","camera"]
items_us = ["Neko Brochure [WIP]","Cat's Paw","Move Up","Move Down","Move Left","Move Right","Move Near","Move Far","Rotate 15 Degrees","Rotate 90 Degrees","Palette","Blank Painting (%sx%s)","Painting (%sx%s)","Linked Painting (%sx%s)","%s Wallpaper","Camera"]
items_cn = ["猫咪手册[WIP]","猫爪","上移","下移","左移","右移","前移","后移","旋转15度","旋转90度","调色板","空白画(%sx%s)","画(%sx%s)","链接画(%sx%s)","%s墙纸","相机"]

misc_ids = ["lamp_post_iron","lamp_post_gold","lamp_post_quartz","candle_holder_iron","candle_holder_gold","candle_holder_quartz","flower_basket_iron","flower_basket_gold","flower_basket_quartz","custom_block"]
miscs_us = ["Iron Lamp Post","Gold Lamp Post","Quartz Lamp Post","Iron Candle Holder","Gold Candle Holder","Quartz Candle Holder","Hanging Plants","Hanging Plants","Hanging Plants","Custom Block"]
miscs_cn = ["铁灯柱","金灯柱","石英灯柱","铁烛台","金烛台","石英烛台","吊盆植物","吊盆植物","吊盆植物","自定义方块"]

tab_ids = ["nekoration_stone_blocks","nekoration_wooden_blocks","nekoration_ornaments"]
tabs_us = ["Stone Blocks","Wooden Blocks","Ornaments"]
tabs_cn = ["石质方块","木质方块","装饰"]

gui_ids = ["button.scroll_up","button.scroll_down","button.enable_all","button.disable_all","button.save_painting","button.save_painting_content","button.load_image","button.clear","paint.tool_param0","paint.tool_param1","paint.tool_param2","paint.tool_param3","message.scroll_change",
        "message.painting_saved","message.painting_content_saved","message.paint_with_palette","message.painting_load_failed","message.link_expired",
        "message.press_key_color_info","message.color_info","message.press_key_debug_info","message.press_key_undo_redo","message.press_key_change_tool","message.press_key_color_picker_on","message.press_key_color_picker_off","message.press_key_change_grid","message.painting_size_warning","message.painting_size_warning_help","message.size",
        "button.enable_glow","button.disable_glow","button.round_brush","button.square_brush","button.transp_add_up","button.transp_overwrite"]
guis_us = ["Scroll Up","Scroll Down","Enable All","Disable All","Save Painting","Save Painting Content","Load Image File","Clear","Pencil Radius","Brush Radius","Eraser Radius","Selection Threshold","Scroll to change %s",
           "Painting saved as %s","Painting content saved as %s","Edit painting with a palette","Failed to load painting %s","Link expired: Failed to find the original painting.",
           "Press %s to toggle Color Info.","Color: %s R:%s G:%s B:%s","Press %s to view debug info.","Press %s to undo/redo.","Press %s to change active tool.","Press %s to show color picker.","Press %s to hide color picker.","Press %s to change grid size.","Painting of this size cannot be saved to a single item.","Use a Linked Painting to copy/move.","Size:  %sx%s",
           "Enable Glowing Text", "Disable Glowing Text","Round Brush", "Square Brush", "Add up Transparency", "Overwrite Transparency"]
guis_cn = ["向上","向下","选中所有","清除所有","保存绘画","保存绘画内容","读取图像文件","清除","铅笔直径","画笔直径","橡皮直径","选区阈值","使用鼠标滚轮调整%s",
           "已将绘画保存至%s","已将绘画内容保存至%s","请使用调色板编辑画作","绘画%s加载失败","链接已失效：无法找到原画作",
           "可按下%s键查看颜色信息","颜色：%s 红：%s 绿：%s 蓝：%s","可按下%s键查看调试信息","可按下%s键撤销/重做","可按下%s键切换工具","可按下%s键显示颜色选择器","可按下%s键隐藏颜色选择器","可按下%s键切换网格尺寸","此大小的绘画信息无法存入单个物品","请使用链接画复制或移动","尺寸:  %sx%s",
           "启用发光文本","禁用发光文本","圆形画刷","方形画刷","叠加透明度","覆盖透明度"]

entity_ids = ["painting","wallpaper","seat"]
entities_us = ["Painting","Wallpaper","Seat"]
entities_cn = ["画","墙纸","座位"]

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
for c_i in range(0, len(color_ids)):
    obj_us['color.nekoration.' + color_ids[c_i]] = colors_us[c_i]
    #obj_us['color.wooden.' + colors[col]] = wooden_us[col]
    obj_cn['color.nekoration.' + color_ids[c_i]] = colors_cn[c_i]
    #obj_cn['color.wooden.' + colors[col]] = wooden_cn[col]

# [WOODEN BLOCKS TAB]
for w_i in range(0, len(wood_ids)):
    wood_id = wood_ids[w_i]
    wood_us = woods_us[w_i]
    wood_cn = woods_cn[w_i]
    for v_i in range(0, len(half_timber_ids)):
        block_id = wood_id + "_half_timber_" + half_timber_ids[v_i]
        obj_us["block.nekoration." + block_id] = "%s " + half_timbers_us[v_i].format(wood=wood_us)
        obj_cn["block.nekoration." + block_id] = "%s" + half_timbers_cn[v_i].format(wood=wood_cn)
    for v_i in range(0, len(window_ids)):
        block_id = wood_id + "_window_" + window_ids[v_i]
        obj_us["block.nekoration." + block_id] = windows_us[v_i].format(wood=wood_us)
        obj_cn["block.nekoration." + block_id] = windows_cn[v_i].format(wood=wood_cn)
    for v_i in range(0, len(furniture_ids)):
        block_id = wood_id + "_" + furniture_ids[v_i]
        obj_us["block.nekoration." + block_id] = furniture_us[v_i].format(wood=wood_us)
        obj_cn["block.nekoration." + block_id] = furniture_cn[v_i].format(wood=wood_cn)

# [STONE BLOCKS TAB]
for s_i in range(0, len(stone_ids)):
    stone_id = stone_ids[s_i]
    stone_us = stones_us[s_i]
    stone_cn = stones_cn[s_i]
    if stone_has_smooth_variant[s_i]:
        for ss_i in range(0, len(smooth_stone_block_ids)):
            block_id = smooth_stone_block_ids[ss_i].format(stone=stone_id)
            obj_us['block.nekoration.' + block_id] = smooth_stone_blocks_us[ss_i].format(stone=stone_us)
            obj_cn['block.nekoration.' + block_id] = smooth_stone_blocks_cn[ss_i].format(stone=stone_cn)
    for ss_i in range(0, len(stone_block_ids)):
        block_id = stone_block_ids[ss_i].format(stone=stone_id)
        obj_us['block.nekoration.' + block_id] = stone_blocks_us[ss_i].format(stone=stone_us)
        obj_cn['block.nekoration.' + block_id] = stone_blocks_cn[ss_i].format(stone=stone_cn)

# [ORNAMENTS TAB]
for d_i in range(0, len(door_ids)):
    obj_us['block.nekoration.' + door_ids[d_i]] = doors_us[d_i]
    obj_cn['block.nekoration.' + door_ids[d_i]] = doors_cn[d_i]

for d_i in range(0, len(decor_ids)):
    obj_us['block.nekoration.' + decor_ids[d_i]] = decors_us[d_i]
    obj_cn['block.nekoration.' + decor_ids[d_i]] = decors_cn[d_i]

for m_i in range(0, len(misc_ids)):
    obj_us['block.nekoration.' + misc_ids[m_i]] = miscs_us[m_i]
    obj_cn['block.nekoration.' + misc_ids[m_i]] = miscs_cn[m_i]

for i_i in range(0, len(item_ids)):
    obj_us['item.nekoration.' + item_ids[i_i]] = items_us[i_i]
    obj_cn['item.nekoration.' + item_ids[i_i]] = items_cn[i_i]

# [TAB NAMES]
for t_i in range(0, len(tab_ids)):
    obj_us['itemGroup.' + tab_ids[t_i]] = "Nekoration: " + tabs_us[t_i]
    obj_cn['itemGroup.' + tab_ids[t_i]] = "猫咪装饰丨" + tabs_cn[t_i]

# [GUI]
for g_i in range(0, len(gui_ids)):
    obj_us['gui.nekoration.' + gui_ids[g_i]] = guis_us[g_i]
    obj_cn['gui.nekoration.' + gui_ids[g_i]] = guis_cn[g_i]

# [ENTITIES]
for e_i in range(0, len(entity_ids)):
    obj_us['entity.nekoration.' + entity_ids[e_i]] = entities_us[e_i]
    obj_cn['entity.nekoration.' + entity_ids[e_i]] = entities_cn[e_i]

# [TOOLTIPS]
obj_us['tooltip.nekoration.horizontal_connect_block'] = "Connects horizontally with matching blocks %s"
obj_cn['tooltip.nekoration.horizontal_connect_block'] = "%s可与同种方块水平连接"
obj_us['tooltip.nekoration.vertical_connect_block'] = "Connects vertically with matching blocks %s"
obj_cn['tooltip.nekoration.vertical_connect_block'] = "%s可与同种方块垂直连接"

obj_us['tooltip.nekoration.connection_neither'] = "in neither direction"
obj_cn['tooltip.nekoration.connection_neither'] = "从两侧均不"
obj_us['tooltip.nekoration.connection_both'] = "in both directions"
obj_cn['tooltip.nekoration.connection_both'] = "从两侧均"
obj_us['tooltip.nekoration.connection_left2right'] = "from left to right"
obj_cn['tooltip.nekoration.connection_left2right'] = "从左至右"
obj_us['tooltip.nekoration.connection_right2left'] = "from right to left"
obj_cn['tooltip.nekoration.connection_right2left'] = "从右至左"
obj_us['tooltip.nekoration.connection_bottom2top'] = "from bottom to top"
obj_cn['tooltip.nekoration.connection_bottom2top'] = "从下至上"
obj_us['tooltip.nekoration.connection_top2bottom'] = "from top to bottom"
obj_cn['tooltip.nekoration.connection_top2bottom'] = "从上至下"

# Set cwd to file directory
script_dir = os.path.dirname(os.path.abspath(__file__))
os.chdir(script_dir)


# Write these objects into files in JSON format
with open("en_us.json", "w+") as f:
    with open(r"brochure\en_us.txt", "r+") as f1:
        obj_us['book.nekoration.intro'] = f1.read()
    data = json.dumps(obj_us, sort_keys=True, indent=4, separators=(',', ': '))
    f.write(data)

with open("zh_cn.json", "w+", encoding='utf-8') as f:
    with open(r"brochure\zh_cn.txt", "r+", encoding="utf-8") as f1:
        obj_cn['book.nekoration.intro'] = f1.read()
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
    'cat ': ('mii ', 1.0),
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
    for c_i in range(0, len(color_ids)):
        obj_lol['color.nekoration.' + color_ids[c_i]] = colors_lol[c_i]
    with open(r"brochure\lol_us.txt", "r+") as f1:
        obj_lol['book.nekoration.intro'] = f1.read()
    data = json.dumps(obj_lol, sort_keys=True, indent=4, separators=(',', ': '))
    f.write(data)
    # Sooo long & THX 4 ALL teh fishez

