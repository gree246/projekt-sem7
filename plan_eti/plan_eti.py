<<<<<<< Updated upstream
from PIL import Image
import requests
from io import BytesIO

images_count=32
image_width=256
image_height=256
for floor in range(-1,10):
    

    images=[[]]
    for x in range(images_count):
        print("poziom: ", floor," pobrano: ",x,"/",images_count)
        images.append([])
        for y in range(images_count):
            response = requests.get("http://campus.pg.edu.pl/media/blueprints/32/"+str(floor)+"/5/"+str(x)+"_"+str(y)+".png")
            img = Image.open(BytesIO(response.content))
            images[x].append(img)

    total_width = images_count*image_width
    total_height = images_count*image_height

    new_im = Image.new('RGB', (total_width, total_height))
    print("poziom: ", floor," tworzenie zdjęcia wyjściowego")
    x_offset = 0
    for x in range(images_count):
        y_offset = total_height
        for y in range(images_count):
            y_offset-=image_height
            new_im.paste(images[y][x], (x*image_width,y_offset))

    new_im.save('eti_floor_'+str(floor)+'.png')
=======
from PIL import Image
import requests
from io import BytesIO

images_count=32
image_width=256
image_height=256
for floor in range(-1,10):
    

    images=[[]]
    for x in range(images_count):
        print("poziom: ", floor," pobrano: ",x,"/",images_count)
        images.append([])
        for y in range(images_count):
            response = requests.get("http://campus.pg.edu.pl/media/blueprints/32/"+str(floor)+"/5/"+str(x)+"_"+str(y)+".png")
            img = Image.open(BytesIO(response.content))
            images[x].append(img)

    total_width = images_count*image_width
    total_height = images_count*image_height

    new_im = Image.new('RGB', (total_width, total_height))
    print("poziom: ", floor," tworzenie zdjęcia wyjściowego")
    x_offset = 0
    for x in range(images_count):
        y_offset = total_height
        for y in range(images_count):
            y_offset-=image_height
            new_im.paste(images[y][x], (x*image_width,y_offset))

    new_im.save('eti_floor_'+str(floor)+'.png')
>>>>>>> Stashed changes
    print("poziom: ", floor," ukończono")