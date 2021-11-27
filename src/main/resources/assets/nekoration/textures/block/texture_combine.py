from PIL import Image, ImageDraw
import glob

base = r'./white_concrete.png'
source = r'./half_timber_shadow/'
target = r'./half_timber_back/'


def trans_paste(bg,fg,box=(0,0)):
    trans = Image.new("RGBA",bg.size)
    trans.paste(fg,box,mask=fg)
    nim = Image.alpha_composite(bg,trans)
    return nim

def main():
    basei = Image.open(base).convert('RGBA')
    box = (0,0,16,16)
    all_1 = glob.glob(source + '*.png')
    for file in all_1:
        print('Processing File: ' + file)
        overlayi = Image.open(file).convert('RGBA')
        resi = trans_paste(basei,overlayi,box)
        org_name = file.split('\\')[-1].split('/')[-1]
        resi.save(target + org_name)
        

if __name__ == '__main__':
    main()
