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
windows_us = ["Simple {wood} Window","Arch {wood} Window","Cross {wood} Window","Lancet {wood} Window","Shade {wood} Window"]
windows_cn = ["简易{wood}窗户","拱形{wood}窗户","十字{wood}窗户","尖头{wood}窗户","遮光{wood}窗户"]
window_pane_ids = ["pane_" + window_id for window_id in window_ids]
window_panes_us = ["Simple {wood} Window Pane","Arch {wood} Window Pane","Cross {wood} Window Pane","Lancet {wood} Window Pane","Shade {wood} Window Pane"]
window_panes_cn = ["简易{wood}窗户板","拱形{wood}窗户板","十字{wood}窗户板","尖头{wood}窗户板","遮光{wood}窗户板"]

furniture_ids = ["easel_menu","table","round_table","glass_table","round_glass_table","chair","armchair","bench","cabinet","drawer","drawer_chest","cupboard","wall_shelf"]
furniture_us = ["%s {wood} Easel Menu","{wood} Table","{wood} Round Table","{wood} Glass Table","{wood} Round Glass Table","{wood} Chair","{wood} Armchair","{wood} Bench","{wood} Cabinet","{wood} Drawer","{wood} Chest of Drawers","{wood} Cupboard","{wood} Wall Shelf"]
furniture_cn = ["%s{wood}展架","{wood}桌子","{wood}圆桌","{wood}玻璃桌","{wood}圆玻璃桌","{wood}椅子","{wood}扶手椅","{wood}长椅","{wood}储物柜","{wood}抽屉","{wood}斗柜","{wood}橱柜","{wood}墙架"]

# Furniture blocks not tied to a wood type (Ornaments tab, Furniture category)
furniture2_ids = ["pumpkin_table","pumpkin_chair"]
furnitures2_us = ["Pumpkin Table","Pumpkin Chair"]
furnitures2_cn = ["南瓜桌","南瓜椅"]

stone_ids = ["stone", "granite", "diorite", "andesite", "calcite", "dripstone", "tuff", "sandstone", "red_sandstone"]
stones_us = ["Stone", "Granite", "Diorite", "Andesite", "Calcite", "Dripstone", "Tuff", "Sandstone", "Red Sandstone"]
stones_cn = ["石头", "花岗岩", "闪长岩", "安山岩", "方解石", "滴水石", "凝灰岩", "砂岩", "红砂岩"]

stone_has_smooth_variant = [False, True, True, True, True, True, True, False, False]
stone_has_polished_variant = [True, False, False, False, True, True, False, True, True]

smooth_stone_block_ids = ["smooth_{stone}", "smooth_{stone}_slab", "smooth_{stone}_stairs"]
smooth_stone_blocks_us = ["Smooth {stone}", "Smooth {stone} Slab", "Smooth {stone} Stairs"]
smooth_stone_blocks_cn = ["平滑{stone}", "平滑{stone}台阶", "平滑{stone}楼梯"]

polished_stone_block_ids = ["polished_{stone}", "polished_{stone}_slab", "polished_{stone}_stairs"]
polished_stone_blocks_us = ["Polished {stone}", "Polished {stone} Slab", "Polished {stone} Stairs"]
polished_stone_blocks_cn = ["磨制{stone}", "磨制{stone}台阶", "磨制{stone}楼梯"]

stone_block_ids = ["polished_smooth_{stone}", "polished_smooth_{stone}_slab", "polished_smooth_{stone}_stairs", "{stone}_bricks", "{stone}_bricks_slab", "{stone}_bricks_stairs", "{stone}_tiles", "{stone}_tiles_slab", "{stone}_tiles_stairs", "chiseled_smooth_{stone}", "horizontal_chiseled_smooth_{stone}", "chiseled_{stone}", "chiseled_{stone}_bricks", "{stone}_base", "{stone}_column_doric", "{stone}_column_ionic", "{stone}_column_corinthian",
    "{stone}_frame_head",  "{stone}_frame_peak","{stone}_frame_side", "{stone}_frame_sill", "{stone}_pot", "{stone}_planter"]
stone_blocks_us = ["Polished Smooth {stone}", "Polished Smooth {stone} Slab", "Polished Smooth {stone} Stairs", "{stone} Bricks", "{stone} Bricks Slab", "{stone} Bricks Stairs", "{stone} Tiles", "{stone} Tiles Slab", "{stone} Tiles Stairs", "Chiseled Smooth {stone}", "Horizontal Chiseled Smooth {stone}", "Chiseled {stone}", "Chiseled {stone} Bricks", "{stone} Base", "{stone} Doric Column", "{stone} Ionic Column", "{stone} Corinthian Column",
    "{stone} Frame Head",  "{stone} Frame Peak","{stone} Frame Side", "{stone} Frame Sill", "{stone} Pot", "{stone} Planter"]
stone_blocks_cn = ["磨制平滑{stone}", "磨制平滑{stone}台阶", "磨制平滑{stone}楼梯", "{stone}砖", "{stone}砖台阶", "{stone}砖楼梯", "{stone}瓦", "{stone}瓦台阶", "{stone}瓦楼梯", "雕纹平滑{stone}", "横向雕纹平滑{stone}", "雕纹{stone}", "雕纹{stone}砖", "{stone}底座", "{stone}多立克柱", "{stone}爱奥尼柱", "{stone}科林斯柱",
    "{stone}框顶边",  "{stone}框尖顶","{stone}框侧边", "{stone}框底边", "{stone}花盆", "{stone}种植盆"]

door_ids = ["quartz_door","chiseled_quartz_door","quartz_bricks_door","tall_quartz_door","tall_chiseled_quartz_door","tall_quartz_bricks_door"]
doors_us = ["Quartz Door","Chiseled Quartz Door","Quartz Bricks Door","Tall Quartz Door","Tall Chiseled Quartz Door","Tall Quartz Bricks Door"]
doors_cn = ["石英门","雕纹石英门","石英砖门","加高石英门","加高雕纹石英门","加高石英砖门"]

decor_ids = ["awning_pure","awning_stripe","short_awning_pure","short_awning_stripe","window_plant"]
decors_us = ["%s Awning","%s Stripe Awning","Short %s Awning","Short %s Stripe Awning","%s Flowering Window Plant"]
decors_cn = ["%s雨篷","%s条纹雨篷","%s短篷","%s条纹短篷","开%s花的窗边植物"]

decortype_ids = ["window_attachment", "furniture", "container", "pots_and_planters", "misc"]
decortypes_us = ["Window Attachment", "Furniture", "Container", "Pots and Planters", "Misc"]
decortypes_cn = ["窗饰", "家具", "收纳", "花盆与种植盆", "杂项"]

item_ids = ["brochure","paw","paw_up","paw_down","paw_left","paw_right","paw_near","paw_far","paw_15","paw_90","palette","painting.blank","painting.painted","painting.magic","wallpaper","camera"]
items_us = ["Neko Brochure [WIP]","Cat's Paw","Move Up","Move Down","Move Left","Move Right","Move Near","Move Far","Rotate 15 Degrees","Rotate 90 Degrees","Palette","Blank Painting (%sx%s)","Painting (%sx%s)","Linked Painting (%sx%s)","%s Wallpaper","Camera"]
items_cn = ["猫咪手册[WIP]","猫爪","上移","下移","左移","右移","前移","后移","旋转15度","旋转90度","调色板","空白画(%sx%s)","画(%sx%s)","链接画(%sx%s)","%s墙纸","相机"]

misc_ids = ["iron_lamp_post","gold_lamp_post","quartz_lamp_post","iron_candle_holder","gold_candle_holder","quartz_candle_holder","iron_flower_basket","gold_flower_basket","quartz_flower_basket","custom_block"]
miscs_us = ["Iron Lamp Post","Gold Lamp Post","Quartz Lamp Post","Iron Candle Holder","Gold Candle Holder","Quartz Candle Holder","Hanging Plants","Hanging Plants","Hanging Plants","Custom Block"]
miscs_cn = ["铁灯柱","金灯柱","石英灯柱","铁烛台","金烛台","石英烛台","吊盆植物","吊盆植物","吊盆植物","自定义方块"]

tab_ids = ["nekoration_cement_blocks","nekoration_stone_blocks","nekoration_wooden_blocks","nekoration_ornaments"]
tabs_us = ["Cement Blocks","Stone Blocks","Wooden Blocks","Ornaments"]
tabs_cn = ["水泥方块","石质方块","木质方块","装饰"]

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
    obj_cn['color.nekoration.' + color_ids[c_i]] = colors_cn[c_i]

# Wood Type Names (for creative tab filter tooltips)...
for w_i in range(0, len(wood_ids)):
    obj_us['wood.nekoration.' + wood_ids[w_i]] = woods_us[w_i]
    obj_cn['wood.nekoration.' + wood_ids[w_i]] = woods_cn[w_i]

# [CEMENT BLOCKS TAB]
cement_ids = ["cement", "trimmed_cement", "paneled_cement", "layered_cement", "cement_frame_head", "cement_frame_peak", "cement_frame_side", "cement_frame_sill", "cement_pot", "cement_planter"]
cements_us = ["%s Cement", "%s Trimmed Cement", "%s Paneled Cement", "%s Layered Cement", "%s Cement Frame Head", "%s Cement Frame Peak", "%s Cement Frame Side", "%s Cement Frame Sill", "%s Cement Pot", "%s Cement Planter"]
cements_cn = ["%s水泥", "%s饰边水泥", "%s镶板水泥", "%s层叠水泥", "%s水泥框顶边", "%s水泥框尖顶", "%s水泥框侧边", "%s水泥框底边", "%s水泥花盆", "%s水泥种植盆"]

for c_i in range(0, len(cement_ids)):
    obj_us['block.nekoration.' + cement_ids[c_i]] = cements_us[c_i]
    obj_cn['block.nekoration.' + cement_ids[c_i]] = cements_cn[c_i]

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
    for v_i in range(0, len(window_pane_ids)):
        block_id = wood_id + "_window_" + window_pane_ids[v_i]
        obj_us["block.nekoration." + block_id] = window_panes_us[v_i].format(wood=wood_us)
        obj_cn["block.nekoration." + block_id] = window_panes_cn[v_i].format(wood=wood_cn)
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
    if stone_has_polished_variant[s_i]:
        for ss_i in range(0, len(polished_stone_block_ids)):
            block_id = polished_stone_block_ids[ss_i].format(stone=stone_id)
            obj_us['block.nekoration.' + block_id] = polished_stone_blocks_us[ss_i].format(stone=stone_us)
            obj_cn['block.nekoration.' + block_id] = polished_stone_blocks_cn[ss_i].format(stone=stone_cn)
    for ss_i in range(0, len(stone_block_ids)):
        block_id = stone_block_ids[ss_i].format(stone=stone_id)
        obj_us['block.nekoration.' + block_id] = stone_blocks_us[ss_i].format(stone=stone_us)
        obj_cn['block.nekoration.' + block_id] = stone_blocks_cn[ss_i].format(stone=stone_cn)

# Vanilla smooth stone has block + slab only; mod adds stairs
obj_us['block.nekoration.smooth_stone_stairs'] = 'Smooth Stone Stairs'
obj_cn['block.nekoration.smooth_stone_stairs'] = '平滑石头楼梯'

# [ORNAMENTS TAB]
for d_i in range(0, len(door_ids)):
    obj_us['block.nekoration.' + door_ids[d_i]] = doors_us[d_i]
    obj_cn['block.nekoration.' + door_ids[d_i]] = doors_cn[d_i]

for d_i in range(0, len(decor_ids)):
    obj_us['block.nekoration.' + decor_ids[d_i]] = decors_us[d_i]
    obj_cn['block.nekoration.' + decor_ids[d_i]] = decors_cn[d_i]

for d_i in range(0, len(decortype_ids)):
    obj_us['decortype.nekoration.' + decortype_ids[d_i]] = decortypes_us[d_i]
    obj_cn['decortype.nekoration.' + decortype_ids[d_i]] = decortypes_cn[d_i]

for f_i in range(0, len(furniture2_ids)):
    obj_us['block.nekoration.' + furniture2_ids[f_i]] = furnitures2_us[f_i]
    obj_cn['block.nekoration.' + furniture2_ids[f_i]] = furnitures2_cn[f_i]

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
obj_us['tooltip.nekoration.connect_block'] = "Connects %s with matching blocks"
obj_cn['tooltip.nekoration.connect_block'] = "可与同种方块%s连接"
obj_us['tooltip.nekoration.connect_block_tip'] = "Place while %s to avoid connection"
obj_cn['tooltip.nekoration.connect_block_tip'] = "%s时放置可阻止连接"
obj_us['tooltip.nekoration.direction_horizontal'] = "horizontally"
obj_cn['tooltip.nekoration.direction_horizontal'] = "水平"
obj_us['tooltip.nekoration.direction_vertical'] = "vertically"
obj_cn['tooltip.nekoration.direction_vertical'] = "垂直"
obj_us['tooltip.nekoration.sneaking'] = "sneaking"
obj_cn['tooltip.nekoration.sneaking'] = "潜行"

# Set cwd to file directory
script_dir = os.path.dirname(os.path.abspath(__file__))
os.chdir(script_dir)


# Write these objects into files in JSON format
with open("en_us.json", "w+") as f:
    with open(r"brochure/en_us.txt", "r+") as f1:
        obj_us['book.nekoration.intro'] = f1.read()
    data = json.dumps(obj_us, sort_keys=True, indent=4, separators=(',', ': '))
    f.write(data)

with open("zh_cn.json", "w+", encoding='utf-8') as f:
    with open(r"brochure/zh_cn.txt", "r+", encoding="utf-8") as f1:
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
    with open(r"brochure/lol_us.txt", "r+") as f1:
        obj_lol['book.nekoration.intro'] = f1.read()
    data = json.dumps(obj_lol, sort_keys=True, indent=4, separators=(',', ': '))
    f.write(data)
    # Sooo long & THX 4 ALL teh fishez
