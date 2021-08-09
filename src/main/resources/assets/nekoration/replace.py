import glob, os

src = r'half_timber_white'
tar = r'half_timber'

all1 = glob.glob(r'opspace\*.json')
for single in all1:
    print(single)
    f = open(single, 'r+', encoding='utf-8')
    lns = f.readlines()
    f.seek(0)
    f.truncate()
    for line in lns:
        line = line.replace(src, tar)
        f.write(line)

    f.close()

    # Rename the files as well...
    if (src in single):
        os.rename(single, single.replace(src, tar))
        print(single + ' -> ' + single.replace(src, tar))
