import format_lang
from constants import FRUITS, STILL_BUSHES
from patchouli import *
from argparse import ArgumentParser
from typing import Optional
from data import hydration_from_rainfall


BOOK_LANGUAGES = ('en_us', 'zh_cn', 'zh_tw', 'ja_jp')
MOD_LANGUAGES = ('en_us', 'de_de', 'ko_kr', 'ru_ru', 'uk_ua', 'zh_cn', 'zh_tw', 'ja_jp')

class LocalInstance:
    INSTANCE_DIR = None

    @staticmethod
    def wrap(rm: ResourceManager):
        def data(name_parts: ResourceIdentifier, data_in: JsonObject, root_domain: str = 'data'):
            return rm.write((LocalInstance.INSTANCE_DIR, '/'.join(utils.str_path(name_parts))), data_in)

        if LocalInstance.INSTANCE_DIR is not None:
            rm.data = data
            return rm
        return None

def main_with_args():
    parser = ArgumentParser('generate_book.py')
    parser.add_argument('--translate', type=str, default='en_us', help='The language to translate to')
    parser.add_argument('--local', type=str, default=None, help='The directory of a local .minecraft to copy into')
    parser.add_argument('--translate-all', type=str, default=None, help='If all languages should be translated')
    parser.add_argument('--format', type=str, default=None, help='Format the mod languages')
    parser.add_argument('--reverse-translate', type=str, default=None, help='Reverse a translation from the mod files.')

    args = parser.parse_args()

    if args.format:
        do_format()
        return

    if args.translate_all:
        do_format()
        for la in BOOK_LANGUAGES:
            main(la, args.local, False, reverse_translate=args.reverse_translate is not None)
    else:
        main(args.translate, args.local, False, reverse_translate=args.reverse_translate is not None)

def do_format():
    # format_lang.main(False, 'minecraft', BOOK_LANGUAGES)
    format_lang.main(False, 'firmalife', MOD_LANGUAGES)

def main(translate_lang: str, local_minecraft_dir: Optional[str], validate: bool, validating_rm: ResourceManager = None, reverse_translate: bool = False):
    LocalInstance.INSTANCE_DIR = local_minecraft_dir

    rm = ResourceManager('tfc', './src/main/resources')
    if validate:
        rm = validating_rm
    i18n = I18n(translate_lang, validate)

    print('Writing book at %s' % translate_lang)
    make_book(rm, i18n, local_instance=False, reverse_translate=reverse_translate)

    i18n.flush()

    if LocalInstance.wrap(rm):
        print('Copying %s book into local instance at: %s' % (translate_lang, LocalInstance.INSTANCE_DIR))
        make_book(rm, I18n(translate_lang, validate), local_instance=True)


# def main():
#     for language in BOOK_LANGUAGES:
#         rm = ResourceManager('tfc', '../src/main/resources')
#         i18n = I18n.create(language)
#
#         print('Writing book %s' % language)
#         make_book(rm, i18n)
#
#         i18n.flush()
#
#         if LocalInstance.wrap(rm) and language == 'en_us':
#             print('Copying into local instance at: %s' % LocalInstance.INSTANCE_DIR)
#             make_book(rm, I18n.create('en_us'), local_instance=True)
#
#         print('Done')

def make_book(rm: ResourceManager, i18n: I18n, local_instance: bool = False, reverse_translate: bool = False):
    book = Book(rm, 'field_guide', {}, i18n, local_instance, reverse_translate)
    book.template('smoking_recipe', custom_component(0, 0, 'SmokingComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('drying_recipe', custom_component(0, 0, 'DryingComponent', {'recipe': '#recipe'}), text_component(0, 45))

    book.category('firmalife', 'Firmalife', 'All about the Firmalife addon', 'firmalife:cured_oven_top', is_sorted=True, entries=(
        entry('differences_from_tfc', 'Differences from TFC', 'tfc:textures/item/food/wheat_bread.png', pages=(
            text('Firmalife makes a few changes to how things operate in regular TFC. This chapter exists to help direct you towards areas where this is very different.'),
            text('$(l:firmalife/cheese)Cheese$() is made through a more complex process. It can be placed in world, and has the option of aging in a $(l:firmalife/cellar)Cellar$().', title='Cheese Aging'),
            text('$(l:firmalife/bread)Bread$() is made through a more complex process, requiring yeast and sweetener. The regular TFC bread recipe makes flatbread, which is worse nutritionally.', title='Bread Making'),
            text('Firmalife has a greater emphasis on sugar. While it can still be obtained through sugar cane, consider using honey (from bees) or making sugar from beets!', title='Sweeteners'),
        )),
        entry('cheese', 'Cheese', 'firmalife:textures/item/food/gouda.png', pages=(
            text('Making $(thing)cheese$() in Firmalife is a little more involved than in vanilla TFC. There are two new kinds of milk: $(thing)Yak Milk$(), and $(thing)Goat Milk$(). These are obtained from milking the $(l:mechanics/animal_husbandry#yak)Yak$() and $(l:mechanics/animal_husbandry#goat)Goat$(), respectively. Milking the $(l:mechanics/animal_husbandry#cow)Cow$() still produces the old kind of milk.'),
            text('Like usual, milk must be $(thing)curdled$() first. To curdle milk, you need $(thing)Rennet$(). Rennet comes from the stomach of $(thing)Ruminant$() animals. This includes $(l:mechanics/animal_husbandry#yak)Yaks$(), $(l:mechanics/animal_husbandry#cow)Cows$(), $(l:mechanics/animal_husbandry#sheep)Sheep$(), $(l:mechanics/animal_husbandry#goat)Goats$(), and $(l:mechanics/animal_husbandry#musk_ox)Musk Oxen$(). To curdle milk, seal it in a $(l:mechanics/barrels)Barrel$() with Rennet for 4 hours.'),
            crafting('firmalife:crafting/cheesecloth', text_contents='Curdled milk must be converted to $(thing)Curds$() by sealing it in a barrel with $(thing)Cheesecloth$(). Cheesecloth is not reusable.'),
            crafting('firmalife:crafting/cheddar_wheel', text_contents='You are ready to make $(thing)Dry Cheese$() if you wish. You can make $(thing)Rajya Metok$() from $(thing)Yak Curds$(), $(thing)Chevre$() from $(thing)Goat Curds$(), and $(thing)Cheddar$() from $(thing)Milk Curds$().'),
            crafting('firmalife:crafting/chevre_wheel', 'firmalife:crafting/rajya_metok_wheel'),
            text('Your other option is to make $(thing)Wet Cheeses$(). These are made by sealing the curds in a barrel of $(thing)Salt Water$(). You can make $(thing)Shosha$() from $(thing)Yak Curds$(), $(thing)Feta$() from $(thing)Goat Curds$(), and $(thing)Gouda$() from $(thing)Milk Curds$().'),
            text('Cheese wheels are blocks that should be placed in order to help them last. To improve their quality and shelf life, cheese wheels should be $(thing)Aged$() in a $(l:firmalife/cellar)Cellar$(). In order to obtain edible cheese from a cheese wheel, it should be sliced off the wheel by clicking $(item)$(k:key.use)$() with a $(thing)Knife$(). If the block is simply broken, the aging is lost!').anchor('aging'),
            multimultiblock('The aging stages of a wheel of Gouda: $(thing)Fresh$(), $(thing)Aged$(), and $(thing)Vintage$().', *[block_spotlight('', '', 'firmalife:gouda_wheel[age=%s]' % age) for age in ('fresh', 'aged', 'vintage')]),
        )),
        entry('climate_station', 'Climate Station', 'firmalife:climate_station', pages=(
            text('The $(thing)Climate Station$() is a block that manages the $(l:firmalife/greenhouse)Greenhouse$() and the $(l:firmalife/cellar)Cellar$(). When its corresponding multiblock is built correctly, it will show water on its sides. When it is invalid, it will show ice. The Climate Station must be placed on the first level of the multiblock, touching a wall. If it is not touching a wall, you may $(item)$(k:key.use)$() it with a block that is part of the multiblock you are trying to make to tell it what to look for.'),
            multimultiblock('The climate station in its valid and invalid state.', *[block_spotlight('', '', 'firmalife:climate_station[stasis=%s]' % b) for b in ('true', 'false')]),
            text('$(li)It updates periodically on its own, or when placed/broken.$()$(li)When a climate station updates, it tells all the blocks inside the multiblock that they can operate. For example, it lets $(l:firmalife/cheese)Cheese$() begin aging.$()$(li)Press $(item)$(k:key.use)$() to force update the Climate Station and the blocks inside the multiblock.', 'Climate Station Tips'),
            crafting('firmalife:crafting/climate_station', text_contents='The climate station is crafted like this.'),
        )),
        entry('cellar', 'Cellars', 'firmalife:sealed_bricks', pages=(
            text('The $(thing)Cellar$() is a multiblock device controlled by a $(l:firmalife/climate_station)Climate Station$(). The Cellar multiblock\'s only requirement is that it be in an enclosed area surrounded by $(thing)Sealed Bricks$() or $(thing)Sealed Brick Doors$() on all sides. The Climate Station must be placed on the first level of the cellar, touching a wall.'),
            multiblock('An Example Cellar', 'This is just one of many cellars that you could make!', True, multiblock_id='firmalife:cellar'),
            crafting('firmalife:crafting/sealed_bricks', 'firmalife:crafting/sealed_door'),
            text('$(thing)Beeswax$() is obtained from $(l:firmalife/beekeeping)Beekeeping$().$(br)Cellars are used for $(l:firmalife/cheese#aging)Aging Cheese$().'),
            text('The cellar is used for food preservation, for example by using $(l:firmalife/food_shelves)Food Shelves$() and $(l:firmalife/hangers)Hangers$(). The cellar performs better in environments with cooler average temperatures for food preservation. Below 0 degrees, decay modifiers work slightly better. Below -12 degrees, they perform much better.'),
            empty_last_page()
        )),
        entry('food_shelves', 'Food Shelves', 'firmalife:wood/food_shelf/pine', pages=(
            text('The $(thing)Food Shelf$() is a device for storing food. It can only be used in a valid $(l:firmalife/cellar)Cellar$(). Food shelves can contain a full stack of one food item. Adding and removing the item can be done with $(item)$(k:key.use)$(). Items in valid food shelves receive a decay modifier that is better than vessels.'),
            crafting('firmalife:crafting/wood/acacia_shelf', text_contents='The food shelf is made from planks and lumber.'),
        )),
        entry('hangers', 'Hangers', 'firmalife:wood/hanger/pine', pages=(
            text('The $(thing)Hanger$() is a device for storing meat or garlic. It can only be used in a valid $(l:firmalife/cellar)Cellar$(). Food shelves can contain a full stack of one item. Adding and removing the item can be done with $(item)$(k:key.use)$(). Items in valid food shelves receive a decay modifier that is better than shelves or vessels.'),
            crafting('firmalife:crafting/wood/acacia_hanger', text_contents='The hanger is made from planks and string.'),
        )),
        entry('jarbnet', 'Jarbnets', 'firmalife:wood/jarbnet/pine', pages=(
            text('The jarbnet is a cosmetic storage block for $(l:tfc:mechanics/jarring)Jars$(), Candles, and Jugs. It can be opened and closed by clicking with an empty hand and $(item)$(k:key.sneak)$() pressed. If candles are inside, it can be lit to produce a small amount of light.'),
            crafting('firmalife:crafting/wood/acacia_jarbnet'),
        )),
        entry('greenhouse', 'Greenhouse', 'firmalife:sealed_bricks', pages=(
            text('The $(thing)Greenhouse$() is a multiblock device controlled by a $(l:firmalife/climate_station)Climate Station$(). It allows growing crops year round. The Greenhouse has an array of types and blocks to choose from. However, building a greenhouse is quite simple. Like the $(l:firmalife/cellar)Cellar$(), it should be an enclosed area of blocks belonging to the same $(thing)Greenhouse Type$(). The floor of the greenhouse may be non-air block.'),
            text('The walls of greenhouses must be solid faces. Panel walls should be placed so that the face that is on the exterior of the block (eg. the face you can place a torch on) faces into the greenhouse. The same applies for roofs, except that slabs are always considered valid roof blocks. Trapdoors and doors are also always valid and require no special placement.'),
            text('$(thing)Greenhouse Types$() are families of greenhouse blocks that can be used interchangeably in a greenhouse. Most greenhouse blocks $(thing)age$(). For example, $(thing)Treated Wood$() greenhouse blocks become $(thing)Weathered Treated Wood$() blocks. Since both of those block types belong to the same greenhouse type, your greenhouse will continue to function.'),
            text('These are the $(thing)Greenhouse Types$() available, with the block types they can age into:$(br)$(br)$(li)Treated Wood: Weathered $()$(li)Copper: Exposed, Weathered, Oxidized$()$(li)Iron: Rusted$() $(li)Stainless Steel (does not age)$()', 'Greenhouse Types'),
            text('There are four types of regular $(thing)Greenhouse Blocks$(): Walls, Doors, Roofs, and Roof Tops. Roofs and Roof Tops are stairs and slabs, respectively. There are also thinner versions of these blocks, also known as panel walls, trapdoors, and panel roofs. These can be combined however you choose to form the structure of the greenhouse.'),
            multimultiblock('An example greenhouse, in each main type.', *[multiblock('', '', True, multiblock_id='firmalife:%s_greenhouse' % g) for g in ('treated_wood', 'copper', 'iron', 'stainless_steel')]),
            text('There are many blocks that operate inside Greenhouses:$(br)$(li)$(l:firmalife/planters)Planters$(), for growing crops$(), and $(br)$(li)$(l:firmalife/irrigation)Sprinklers$(), various devices that add water to planters.'),
            text('The next four pages contain recipes for the main greenhouse block types. While they are only shown for Iron greenhouses, the iron rods in the recipe can be replaced with $(thing)Treated Lumber$(), $(thing)Copper Rods$(), or $(thing)Stainless Steel Rods$(). For information on Stainless Steel, see $(l:firmalife/stainless_steel)this linked page$().'),
            crafting('firmalife:crafting/greenhouse/iron_greenhouse_wall', 'firmalife:crafting/greenhouse/iron_greenhouse_roof'),
            crafting('firmalife:crafting/greenhouse/iron_greenhouse_door', 'firmalife:crafting/greenhouse/iron_greenhouse_roof_top'),
            crafting('firmalife:crafting/greenhouse/iron_greenhouse_trapdoor', 'firmalife:crafting/greenhouse/iron_greenhouse_panel_roof'),
            crafting('firmalife:crafting/greenhouse/iron_greenhouse_panel_wall', 'firmalife:crafting/greenhouse/iron_greenhouse_port'),
        )),
        entry('irrigation', 'Irrigation', 'firmalife:sprinkler', pages=(
            text('The $(thing)Sprinkler$() is a device that sprinkles water in a 5x6x5 area centered on the block below the sprinkler block. You know it is working when it drips out water particles. Sprinklers placed facing up irrigate the same 5x6x5 area above.'),
            anvil_recipe('firmalife:anvil/sprinkler', 'The sprinkler is made with a $(thing)Copper Sheet$().'),
            text('Sprinklers must be connected to a system of pipes that feed it water in order to work. This is done by connecting a series of $(thing)Copper Pipes$() to them. Copper Pipes transport water up to 32 blocks to a sprinkler. They are connected to $(thing)Irrigation Tanks$() or $(thing)Pumping Stations$().'),
            anvil_recipe('firmalife:anvil/copper_pipe', 'The copper pipe is made with a sheet.'),
            two_tall_block_spotlight('', '', 'firmalife:pumping_station', 'firmalife:irrigation_tank'),
            text('Pumping stations must be above a source block of water in order to work, and be connected to mechanical power. Irrigation tanks can also serve water through their ports on the sides, provided that they are stacked at most 3 blocks high above a pumping station on other tanks.'),
            crafting('firmalife:crafting/pumping_station', 'firmalife:crafting/irrigation_tank'),
            crafting('firmalife:crafting/oxidized_copper_pipe', text_contents='Oxidized pipes are the same as regular copper pipes, except they do not connect to the other kind of pipe.'),
            crafting('firmalife:crafting/greenhouse/iron_greenhouse_port', text_contents='Greenhouse ports have a single pipe inside of them. They can be used to pass water through the walls of greenhouses!'),
            empty_last_page(),
        )),
        entry('planters', 'Planters', 'firmalife:large_planter', pages=(
            text('$(thing)Planters$() are used to grow crops inside a $(l:firmalife/greenhouse)Greenhouse$(). To see the status of a planter, you can look at it while holding a $(thing)Hoe$(). Crops in planters consume $(l:mechanics/fertilizers)Nutrients$() in a similar way to $(l:mechanics/crops)Crops$(). Planters should be placed inside a valid Greenhouse and activated with a $(l:firmalife/climate_station)Climate Station$(). Planters need at least some natural sunlight to work.').anchor('planters'),
            crafting('firmalife:crafting/watering_can', text_contents='Planters must be $(thing)Watered$() to grow. This is done with a $(thing)Watering Can$(), crafted from a $(thing)Wooden Bucket$(), a container of $(thing)Water$() and $(thing)Lumber$(). Press $(item)$(k:key.use)$() with it to water nearby planters. Refill it by pressing $(item)$(k:key.use)$() on a water source.'),
            crafting('firmalife:crafting/large_planter', text_contents='$(thing)Large Planters$() are the most simple kind of planter. They grow a single crop from seed, and are harvested with $(item)$(k:key.use)$() when mature.'),
            text('Large Planters can grow $(thing)Green Beans$(), $(thing)Tomatoes$(), $(thing)Sugarcane$(), $(thing)Jute$(), and $(thing)Grains$(). However, to grow Grains, you need a $(thing)Copper$() or better Greenhouse.'),
            crafting('firmalife:crafting/quad_planter', text_contents='$(thing)Quad Planters$() grow four individual crops at once. These crops all draw from the same nutrient pool, and can be harvested individually with $(item)$(k:key.use)$() when mature.'),
            text('Quad Planters can grow $(thing)Beets$(), $(thing)Cabbage$(), $(thing)Carrots$(), $(thing)Garlic$(), $(thing)Onions$(), $(thing)Potatoes$(), and $(thing)Soybeans$(). These crops can be grown in any greenhouse type.'),
            crafting('firmalife:crafting/bonsai_planter', text_contents='$(thing)Bonsai Planters$() grow small fruit trees from their saplings. The fruit can be picked with $(item)$(k:key.use)$().'),
            text('Bonsai Planters can grow any fruit tree type, except $(thing)Bananas$(), which need a $(thing)Hanging Planter$(). They all consume Nitrogen as their main nutrient. They need an $(thing)Iron$() or better greenhouse to grow.'),
            crafting('firmalife:crafting/hanging_planter', text_contents='$(thing)Hanging Planters$() grow crops upside down. When mature, they can be harvested with $(item)$(k:key.use)$().'),
            text('Hanging Planters grow $(thing)Squash$(), from their seeds, and $(thing)Bananas$(), from their saplings. Squash can be grown in any greenhouse, but Bananas require an $(thing)Iron$() or better greenhouse to grow. Hanging planters need to anchor to a solid block above them.'),
            crafting('firmalife:crafting/trellis_planter', text_contents='$(thing)Trellis Planters$() grow berry bushes. Berries can be picked with $(item)$(k:key.use)$().'),
            text('Trellis Planters have the unique property of $(thing)propagating$() berry bushes. If a trellis planter is placed on top of another, and the one below has a mature berry bush, it has a chance to grow upwards into the next one. Trellis planters can grow any berry bush except $(thing)Cranberries$(), but require an $(thing)Iron$() or better greenhouse to work. Bushes prefer Nitrogen.'),
            crafting('firmalife:crafting/hydroponic_planter', text_contents='$(thing)Hydroponic Planters$() grow rice and cranberry bushes. They work the same as a quad planter, except that they do not need to be watered. Instead, they must have an $(li)$(l:firmalife/irrigation)Sprinkler Pipe$() below them that is supplying water. Without the pipe they will not grow.'),
            empty_last_page(),
        )),
        entry('beekeeping', 'Beekeeping', 'firmalife:beehive', pages=(
            text('$(thing)Beehives$() are a place to house bees. Beehives need $(thing)Beehive Frames$() inside them for the bees to live. Removing frames from an active hive will cause the bees to attack you, unless done at night, or with a $(thing)Firepit$() underneath the hive active. Beehives can share flowers. The benefit of flowers diminishes after 60 flowers.'),
            crafting('firmalife:crafting/beehive', 'firmalife:crafting/beehive_frame'),
            text('Beehives know about the area in a 5 block radius from them. If there are at least 10 flowers around the hive, there is a chance an empty frame will be populated with a $(thing)Queen$(). This is indicated by bee particles flying around the hive. Having 4 empty frames in a hive greatly increases the chances of bees moving in.'),
            text('If a beehive has two frames with queens, and an empty frame, the two colonies have a chance of $(thing)Breeding$() and producing a new queen in the empty frame. This has the effect of passing on the $(thing)Abilities$() of each parent to the offspring. Abilities are different traits bees have that change how they effect the world around them. They are on a scale of 1-10, with 10 being the max.'),
            crafting('firmalife:crafting/honey_jar_open', text_contents='Bees also produce $(thing)Honey$(). Using $(item)$(k:key.use)$() with an Empty Jar on a hive that visibly has honey gives you a $(thing)Honey Jar$(). Opening a Honey Jar gives you $(thing)Raw Honey$(), a $(thing)Sugar$() substitute.'),
            text('Pressing $(item)$(k:key.use)$() a filled frame in your inventory with a $(thing)Knife$() gives you $(thing)Beeswax$(), which has many uses. However, this kills the queen inside the frame so be careful!'),
            crafting('firmalife:crafting/treated_lumber', text_contents='The most important use of beeswax is in creating $(thing)Treated Lumber$().'),
            text('$(li)Bees can help fertilize planters!$()$(li)Scraping a frame sacrifices the queen. Be smart!$()$(li)Being wet prevents bees from attacking you.$()', 'Bee Tips'),
            text('$(li)$(thing)Hardiness$(): Allows bees to produce honey at lower temperatures. Hardiness 10 allows up to -16°C, whereas Hardiness 1 allows up to 2°C.$()$(li)$(thing)Production$(): Improves the speed of honey production.$()$(li)$(thing)Mutant$(): Increases variability in the traits passed during breeding$().', 'List of Abilities'),
            text('$(li)$(thing)Fertility$(): Increases likelihood of breeding.$()$(li)$(thing)Crop Affinity$(): Likelihood of spreading a small amount of nutrients to crops.$()$(li)$(thing)Nature Restoration$(): Causes new flowers and lilypads to spawn around the hive.$()$(li)$(thing)Calmness$(): Decreases likelihood of bees attacking you$().'),
            text('Bees with high Mutant ability have a chance of developing a $(thing)Genetic Disease$(). Diseased bees pass on their disease to their offspring, and don\'t produce honey.'),
            empty_last_page(),
        )),
        entry('stainless_steel', 'Stainless Steel', 'firmalife:metal/ingot/stainless_steel', pages=(
            text('$(thing)Stainless Steel$() and $(thing)Chromium$() are $(thing)Steel-tier$() metals added by Firmalife. They are used in the construction of $(l:firmalife/greenhouse)Stainless Steel Greenhouses$().'),
            alloy_recipe('Stainless Steel', 'firmalife:metal/ingot/stainless_steel', ('Chromium', 20, 30), ('Nickel', 10, 20), ('Steel', 60, 80), text_content=''),
            item_spotlight('firmalife:ore/small_chromite', text_contents='Chromite is an ore that is melted to obtain Chromium. It is found in $(thing)Igneous Intrusive$() and $(thing)Metamorphic$() rocks.'),
            text('$(li)Granite$()$(li)Diorite$()$(li)Gabbro$()$(li)Slate$()$(li)Phyllite$()$(li)Schist$()$(li)Gneiss$()$(li)Marble$()', 'All Chromium Rocks')
        )),
        entry('drying', 'Drying', 'firmalife:drying_mat', pages=(
            text('The $(thing)Drying Mat$() is used to dry items. It is made with $(thing)Fruit Leaves$(), which are obtained from breaking the leaves of $(thing)Fruit Trees$().'),
            crafting('firmalife:crafting/drying_mat', text_contents='The recipe for the drying mat.'),
            text('To use the drying mat, place it out on the sun and add an item to it with $(item)$(k:key.use)$(). After a half day, it will be dried. If it rains, the drying process must start over.'),
            crafting('firmalife:crafting/solar_drier', text_contents='The solar drier functions the same as the drying mat, but 12x as fast.'),
            drying_recipe('firmalife:drying/drying_fruit', 'Drying fruit is a common use of the drying mat. Dried fruit is used in some recipes, and lasts longer.'),
            drying_recipe('firmalife:drying/tofu', 'Tofu is made using a drying mat.'),
            drying_recipe('firmalife:drying/cinnamon', 'Cinnamon is made using a drying mat.'),
            empty_last_page()
        )),
        entry('smoking', 'Smoking', 'tfc:textures/item/food/venison.png', pages=(
            text('Wool string is used to hang items for $(thing)Smoking$(). To place it, just use $(item)$(k:key.use)$().'),
            two_tall_block_spotlight('Smoking', 'A piece of string above a firepit.', 'tfc:firepit[lit=true]', 'firmalife:wool_string'),
            text('Smoking is used to preserve $(thing)Meat$() and $(l:mechanics/dairy)Cheese$(). To smoke meat, it must have first been $(thing)Brined$() by sealing it in a $(thing)Barrel$() with $(thing)Brine$(). You may also salt it first. Cheese does not have this requirement.'),
            text('To start the smoking process, add the item to the string above a firepit. The firepit must be within four blocks, directly underneath the string. The string should begin to generate some smoke if it is working. It is important to note that the firepit must only be burned with $(thing)Logs$(). Using something like $(thing)Peat$() will instantly give your food the $(thing)Disgusting$() trait!'),
            text('The smoking process takes 8 in-game hours. Happy smoking!'),
            empty_last_page()
        )),
        entry('ovens', 'Ovens', 'firmalife:cured_oven_top', pages=(
            text('$(thing)Ovens$() are a great way of cooking lots of food in a way that improves their shelf life. Oven-baked food decays at 90% of the rate of regular food. Ovens are a multiblock structure consisting of a $(thing)Bottom Oven$(), $(thing)Top Oven$(), and optionally $(thing)Chimneys$(). These blocks start off as clay, and must be $(thing)Cured$() by raising their temperature to a certain amount for long enough.$(br)$(l:firmalife/oven_appliances)Oven Appliances$() extend oven functionality.'),
            knapping('firmalife:clay_knapping/oven_top', 'The recipe for the top oven.'),
            knapping('firmalife:clay_knapping/oven_bottom', 'The recipe for the bottom oven.'),
            knapping('firmalife:clay_knapping/oven_chimney', 'The recipe for the oven chimney    .'),
            crafting('tfc:crafting/bricks', text_contents='Ovens are insulated with $(thing)Bricks$(), other oven blocks, or anything that can insulate a Forge. This means you can use stone blocks, if you want!'),
            crafting('firmalife:crafting/peel', text_contents='The $(thing)Peel$() is the only safe way to remove hot items from an Oven. Just $(item)$(k:key.use)$() on it while holding it to retrieve items. Otherwise, you may get burned!'),
            text('The Oven first consists of the Top Oven placed on top of the Bottom Oven. All sides of each oven part, besides the front face, should then be covered with Oven Insulation blocks, as covered two pages ago. You may choose to use $(thing)Oven Chimneys$() as insulation. Placing a stack of chimneys directly behind the oven causes the smoke from the oven to travel up and out of it. If you don\'t do this, smoke will quickly fill up your house, which is very distracting!'),
            multimultiblock('An example oven structure, uncured and cured.', *[multiblock('', '', True, (
                ('     ', '  C  '),
                ('     ', '  C  '),
                ('WT0TW', 'WWCWW'),
                ('WBBBW', 'WWCWW'),
            ), {
                '0': 'firmalife:%soven_top[facing=north]' % pref,
                'T': 'firmalife:%soven_top[facing=north]' % pref,
                'B': 'firmalife:%soven_bottom[facing=north]' % pref,
                'W': 'minecraft:bricks',
                'C': 'firmalife:%soven_chimney' % pref,
            }) for pref in ('cured_', '')]),
            text('The Bottom Oven is used to hold fuel, which may only be logs. Press $(item)$(k:key.use)$() to add or remove them. The bottom oven is also the part of the oven which may be lit with a $(thing)Firestarter$() or other tool. It transfers heat contained in it to the top oven.'),
            text('The Top Oven contains the items that are being cooked. It will draw heat from the Bottom Oven and slowly release it over time. This means that even if your fuel runs out, your Top Oven can continue to work for a little while. Adding items to it is as simple as pressing $(item)$(k:key.use)$(). Remember to use a $(thing)Peel$() to remove the items after.'),
            text('Curing Oven blocks is easy, but requires patience. Simply start running your Bottom Oven as you would normally, and then wait. If an oven block is above 600 degrees for about 80 seconds, it will cure itself and any oven blocks around it. The curing effect will pass all the way up chimneys nearby.'),
            crafting('firmalife:crafting/oven_insulation', text_contents='Crafting oven insulation for your Bottom Oven allows you to remove the need for insulating it on the sides and back. It does not remove the need for the chimney. Use $(item)$(k:key.use)$() to apply it.'),
            crafting('firmalife:crafting/brick_countertop', text_contents='Countertops are aesthetic blocks that count as oven insulation, and have an appearance that matches that of oven blocks. They are a nice aesthetic choice for your kitchen.'),
            text('Ovens also have $(thing)Finishes$() that can be used to change their appearance. These finishes are applied to the basic brick stage of the oven (or brick blocks themselves), and are cosmetic. Finishes can be mixed and matched. They are applied with $(item)$(k:key.use)$().'),
            crafting('firmalife:crafting/rustic_finish', 'firmalife:crafting/stone_finish'),
            crafting('firmalife:crafting/tile_finish')
        )),
        entry('oven_appliances', 'Oven Appliances', 'firmalife:vat', pages=(
            text('$(l:firmalife/ovens)Ovens$() have a number of devices that interact with them, that extend their functionality. This is because ovens are modular in nature.'),
            crafting('firmalife:crafting/oven_hopper', text_contents='The $(thing)Oven Hopper$() will input logs into any Bottom Oven that it is facing. It holds 16 logs (4 stacks of 4, like a log pile), and its inventory is fed by dropping items in the top. It can also be fed via automation from other mods.'),
            crafting('firmalife:crafting/ashtray', text_contents='The $(thing)Ashtray$() collects $(thing)Wood Ash$() when placed below a $(thing)Bottom Oven Block$(). There is a 0.5 chance it gains ash when fuel is consumed. Ash is extracted with $(item)$(k:key.use)$() and inserted via attacking it.'),
            crafting('firmalife:crafting/vat', text_contents='The $(thing)Vat$() produces some select boiling recipes in bulk. It has one slot for items, and 10,000mB of fluid space, similar to a barrel.').anchor('vat'),
            text('For example, the vat can be used to make $(thing)Olive Oil Water$() using a ratio of 1 Olive Paste to 200 mB Water. To use a vat, $(item)$(k:key.use)$() it with fluids and items to add them to the inventory. With an empty hand and $(item)$(k:key.sneak)$() held, click to seal and unseal the vat. A vat will not boil until it is sealed.'),
            text('Vats should be placed on the block above a $(thing)Bottom Oven$(). If the vat would overflow on completion of the recipe, it will not boil, so be sure not to overfill it -- especially with recipes that produce more fluid than they consume!'),
            two_tall_block_spotlight('', '', 'firmalife:cured_oven_bottom', 'firmalife:vat'),
            text('Pots and Grills from TFC can be placed on top of a $(thing)Bottom Oven$(). These devices will get heat automatically from the bottom oven. The pot is only able to be used for making soup. It cannot execute regular pot recipes.'),
        )),
        entry('bread', 'Bread', 'tfc:textures/item/food/barley_bread.png', pages=(
            text('To make $(thing)Bread$(), one first must get $(thing)Yeast$(). To get your first yeast, seal $(l:firmalife/drying)Dried Fruit$() in a Barrel of $(thing)Water$(). After three days, $(thing)Yeast Starter$() will form.$(br)From now on, your yeast can be fed by sealing Yeast Starter in a Barrel with $(thing)Flour$(). This causes it to multiply. 1 flour per 100mB of Yeast produces 600mB of Yeast. That\'s a good deal!'),
            crafting('firmalife:crafting/barley_dough', text_contents='Yeast Starter, Sweetener, and Flour can be combined to make $(thing)Dough$(). Dough can be cooked like normal to produce $(thing)Bread!$().'),
            crafting('firmalife:crafting/barley_slice', text_contents='Once baked, you can use a $(thing)knife$() to cut bread into $(thing)slices$(). These can then either be used for $(l:tfc:mechanics/sandwiches)sandwich making$(), or cooked into $(thing)toast$() which can be spread with $(thing)butter$() or preserves.', title='Sliced Bread'),
            crafting('firmalife:crafting/toast_with_butter', 'firmalife:crafting/toast_with_jam', title='Toast')
        )),
        entry('more_fertilizer', 'More Fertilizer Options', 'firmalife:compost_tumbler', pages=(
            text('Given a greater need for fertilization in Firmalife, there are more options for getting $(l:mechanics/fertilizers)fertilizers$().'),
            drying_recipe('firmalife:drying/dry_grass', 'Thatch can be $(l:firmalife/drying)Dried$() into $(thing)Dry Grass$(), which can be used in a Composter as a brown item.'),
            text('$(thing)Compost Tumblers$() are a great way to produce more fertilizer. They must be connected to mechanical power in order to work. It can only be interacted with when not powered, so consider connecting it to a clutch!'),
            crafting('firmalife:crafting/compost_tumbler', text_contents='The compost tumbler is unique in that it takes more types of compost, and does not require precise ratios in order to work.'),
            text('The tumbler can take green and brown items like a regular composter. It can also take pottery sherds, charcoal, fish, and bones in small amounts.'),
            crafting('firmalife:crafting/pottery_sherd', text_contents='Smashing pottery with a hammer yields sherds.'),
            text('While the regular composter takes 16 \'compost units\', the tumbler can hold 32. Green and brown items count the same, being on the range 1-4, but the new additions like fish always count for 1.'),
            text('Adding too much weird stuff to the composter causes it to produce rotten compost. Further, you will not know it is rotten until the very end! If the compost is more than fifteen percent bones, fish, or pottery, or more than twenty percent charcoal. it will rot. Or, if there are 10 or more green units than brown units, it will rot.'),
            text('Favorable amounts of certain additions can extend or shorten the length of time it takes for the compost to complete. Play around with it and see what happens.$(br)If 32 units are in the composter, 3 compost will be produced. If at least 24, 2 compost will be made. If 16 or more, 1 will be made. Below that, and there will be no compost.'),
            empty_last_page(),
        )),
        entry('mixing_bowl', 'Mixing Bowl', 'firmalife:mixing_bowl', pages=(
            text('The mixing bowl is a way of mixing items and fluids together in a friendly way. $(item)$(k:key.use)$() on it with a $(thing)Spoon$() to add it to the bowl, which allows it to operate.'),
            crafting('firmalife:crafting/mixing_bowl', text_contents='Requires a $(thing)Spoon$() to use.'),
        )),
        entry('herbs_and_spices', 'Herbs and Spices', 'firmalife:spice/basil_leaves', pages=(
            text('In Firmalife, there are a number of small plants you can collect and grow on your own, which have cooking properties. The easiest way to obtain these plants is with a $(thing)Seed Ball$(). To use a $(thing)Seed Ball$(), just $(item)$(k:key.use)$() to throw it, like a snowball. This will spawn $(thing)Butterfly Grass$() in the area.'),
            crafting('firmalife:crafting/seed_ball', text_contents='The recipe for the seed ball requires $(l:firmalife/more_fertilizer)Compost$() and 4 $(thing)Seeds$().'),
            block_spotlight('', 'A butterfly grass plant.', 'firmalife:plant/butterfly_grass'),
            text('Butterfly grass will mature over time. When one reaches maturity, it has a chance to spread to surrounding blocks, or turn into something new. Butterfly grass blocks that have been spread by another grass block do not spread anymore.'),
            block_spotlight('', 'Basil is one of the plants that can be spawned by butterfly grass.', 'firmalife:plant/basil'),
            crafting('firmalife:crafting/basil_leaves', text_contents='Basil leaves are used in pizza.'),
        )),
        entry('fruit_trees', 'Firmalife Fruits', 'firmalife:plant/fig_sapling', pages=(
            text('Firmalife adds some fruiting plants on top of those added by TFC.'),
            text('To improve readability, entries start on the next page.'),
            *detail_fruit_tree('cocoa', 'Cocoa trees are used to make $(l:firmalife/chocolate)Chocolate$().'),
            *detail_fruit_tree('fig'),
        )),
        entry('berry_bushes', 'Berry Bushes', 'firmalife:plant/pineapple_bush', pages=(
            text('Firmalife adds some berry bushes. For information on wild grape bushes, see $(l:firmalife/wine)winemaking$().'),
            item_spotlight('firmalife:food/nightshade_berry', text_contents='First is nightshade. Nightshade is a poisonous berry. When put into soup, it makes poisonous $(thing)Stinky Soup$(). It is found between 200-400mm of rain and 7-24 C temperature in forests.'),
            item_spotlight('firmalife:food/pineapple', text_contents='Pineapple bushes are found 250-500mm of rainfall and 20-32 C temperature in forests. Pineapples are like any other fruit, except that they can be made into $(thing)Pineapple Leather$().'),
            crafting('firmalife:crafting/pineapple_fiber', text_contents='Pineapples that have been $(l:firmalife/drying)Dried$() can be crafted into pineapple fiber.'),
            crafting('firmalife:crafting/pineapple_yarn', text_contents='Pineapple yarn is made by crafting a $(thing)Spindle$() with the fiber.'),
            loom_recipe('firmalife:loom/pineapple_leather', text_content='Finally, pineapple leather can be me woven in a $(l:tfc:mechanics/weaving)Loom$(). It is a plant substitute for regular leather than can be used for knapping, crafting, and other uses!')
        )),
        entry('chocolate', 'Chocolate', 'firmalife:textures/item/food/dark_chocolate.png', pages=(
            text('$(thing)Chocolate-making$() takes a few processing steps, for not much of a reward. It\'s important to remember, when playing Firmalife, that being a chocolatier is for your personal enjoyment and pleasure, rather than for trying to extract maximum value from any given input.'),
            text('To start chocolate processing, cocoa beans must first be $(thing)roasted$() in an $(l:firmalife/ovens)Oven$() to make $(thing)Roasted Cocoa Beans$(). Then, craft the roasted beans with a $(thing)Knife$() to split the beans into $(thing)Cocoa Powder$() and $(thing)Cocoa Powder$().'),
            text('The $(l:firmalife/mixing_bowl)Mixing Bowl$() is used to mix cocoa powder, butter, and sweetener (sugar or honey) to make $(thing)Chocolate Blends$(). The ratio of cocoa butter to powder determines what comes out:$(br)$(li)1 Powder, 1 Butter, 1 Sweetener: Milk Chocolate$()$(li)2 Powder, 1 Sweetener: Dark Chocolate$()$(li)2 Butter, 1 Sweetener: White Chocolate$()'),
            drying_recipe('firmalife:drying/dark_chocolate', 'Finally, chocolate is dried on a $(l:firmalife/drying)Drying Mat$() to make $(thing)Chocolate$().')
        )),
        entry('wine', 'Winemaking', 'firmalife:textures/item/food/white_grapes.png', pages=(
            text('$(thing)Winemaking$() is the science of turning grapes into alcohol. There is time spent gathering resources, and spending time crafting those resources together, as well as time spent $(thing)enjoying$() the product. Please note that wine in Firmalife (on its own) has no special use beyond regular TFC alcohol. You should make it only if you want to have $(thing)fun$().'),
            item_spotlight('firmalife:plant/wild_red_grapes', text_contents='Red grapes spawn from 0-30 C, and 125-500 rainfall, or almost the entire habitable area.'),
            item_spotlight('firmalife:plant/wild_white_grapes', text_contents='White grapes spawn from 0-30 C, and 125-500 rainfall, or almost the entire habitable area.'),
            crafting('firmalife:crafting/grape_trellis_post', text_contents='Grapes must be grown on trellises constructed from these special posts and jute fiber.'),
            text('To construct a trellis, place two posts on top of each other. Move two blocks to the left or right and repeat the action. Then, $(item)$(k:key.use)$() the side of one of the top and bottom posts with $(thing)Jute Fiber$() to string lines between the posts. Grape trellises can be chained horizontally to create rows of grapes.'),
            multimultiblock('A grape trellis.',
            multiblock('', '', False, (('X0X'), ('XYX'),), {'X': 'firmalife:grape_trellis_post[axis=x,string_plus=true,string_minus=true]', '0': 'firmalife:grape_string_plant_red[axis=x,lifecycle=healthy,stage=0]', 'Y': 'firmalife:grape_string[axis=x]'}),
                multiblock('', '', False, (('X0X'), ('ZYZ'),), {'X': 'firmalife:grape_trellis_post[axis=x,string_plus=true,string_minus=true]', '0': 'firmalife:grape_string_plant_red[axis=x,lifecycle=healthy,stage=2]', 'Y': 'firmalife:grape_string_red[axis=x,lifecycle=healthy]', 'Z': 'firmalife:grape_trellis_post_red[axis=x,lifecycle=healthy,string_plus=true,string_minus=true]'}),
            ),
            text('Provided the climate requirements are satisfied, the grape will grow up and over the trellis over the course of a few months. It will fruit in the month of July, flowering the month prior. Grapes can the be harvested. Grapes can also be grown in greenhouses on trellises.'),
            crafting('firmalife:crafting/wood/acacia_stomping_barrel', text_contents='The stomping barrel is used to smash grapes. A quern may also be used.'),
            text('To use a stomping barrel, $(item)$(k:key.use)$() with fresh grapes. Then, jump up and down on the barrel 16 times. $(item)$(k:key.use)$() with an empty hand to retrieve the items.$(br2)Then, seal the grapes in a barrel for 5 days to $(thing)ferment$() them.'),
            crafting('firmalife:crafting/wood/acacia_barrel_press', text_contents='The barrel press is the last step in grape processing.'),
            text('The leftmost slot can contain up to 16 grapes. Four grape items are needed for a bottle of wine. The four central slots are for mixing the grapes with other ingredients, but this is optional. Using only red or white grapes yields red or white wine. Adding at least one red grape to white wine makes Rose. Adding sugar to white wine makes dessert wine.'),
            glassworking_recipe('firmalife:glassworking/olivine_wine_bottle', 'Wine must be bottled in a proper wine bottle, made of non-silica glass.'),
            crafting('firmalife:crafting/bottle_label', text_contents='$(thing)Bottle labels$() can be renamed in a scribing station, and will add their name to the wine\'s tooltip.'),
            text('Wine must be provided with a $(thing)Cork$(), made by soaking $(thing)Treated Lumber$() in $(thing)Limewater$() for a day. When all is complete, use the bottle slot to fill the wine, or by pressing $(item)$(k:key.use)$() with a bottle in hand.'),
        )),
        entry('wine_consumption', 'Wine Consumption', 'firmalife:textures/item/olivine_wine_bottle.png', pages=(
            text('The discerning sommelier will be able to detect subtleties in the wine that is produced under different conditions. Indeed, this is possible in the world of Firmalife as well. It starts in the fields in which those grapes were grown -- a row of grapes is deeply affected by the ambient environment and soil.'),
            text('Grapes can have three terrain related traits -- \'Gravel Grown\', \'Slope Grown\', and \'Dirt Grown\', based on the environment nearby. Wine also records the Koppen Climate Classification of the area in which it is bottled. Grapes grown near bees have the \'Bee Pollinated\' trait.'),
            text('Wine begins aging as soon as it is bottled, and stops aging when the cork is removed. The cork can be removed by $(item)$(k:key.use)$() on the bottle item with a knife item. Otherwise, wine bottles work a little like buckets, and can be emptied into barrels or other devices.'),
            crafting('firmalife:crafting/wood/pine_keg', text_contents='The $(thing)Keg$() is a 2x2x2 barrel block that can contain loads of items or fluids. Perfect for your vinery!'),
            crafting('firamlife:crafting/wood/hickory_wine_shelf', text_contents='The $(thing)Wine Shelf$() is the perfect accessory for your vinery, allowing you to display and store your wine bottles in style.'),
            empty_last_page(),
        ))
    ))

    book.build()

# Firmalife Pages

def knapping(recipe: str, text_content: TranslatableStr) -> Page: return recipe_page('knapping_recipe', recipe, text_content)

def drying_recipe(recipe: str, text_content: str) -> Page:
    return page('drying_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))

def smoking_recipe(recipe: str, text_content: str) -> Page:
    return page('smoking_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))

def alloy_recipe(title: str, ingot: str, *components: Tuple[str, int, int], text_content: str) -> Page:
    recipe = ''.join(['$(li)%d - %d %% : $(thing)%s$()' % (lo, hi, alloy) for (alloy, lo, hi) in components])
    return item_spotlight(ingot, title, False, '$(br)$(bold)Requirements:$()$(br)' + recipe + '$(br2)' + text_content)

def custom_component(x: int, y: int, class_name: str, data: JsonObject) -> Component:
    return Component('patchouli:custom', x, y, {'class': 'com.eerussianguy.firmalife.compat.patchouli.' + class_name, **data})

def detail_fruit_tree(fruit: str, text_contents: str = '', right: Page = None, an: str = 'a') -> Tuple[Page, Page, Page]:
    data = FRUITS[fruit]
    left = text('$(bold)$(l:the_world/climate#temperature)Temperature$(): %d - %d °C$(br)$(bold)$(l:mechanics/hydration)Rainfall$(): %d - %dmm$(br2)%s' % (data.min_temp, data.max_temp, data.min_rain, data.max_rain, text_contents), title=('%s tree' % fruit).replace('_', ' ').title()).anchor(fruit)
    if right is None:
        right = multimultiblock('The monthly stages of %s %s tree' % (an, fruit.replace('_', ' ').title()), *[two_tall_block_spotlight('', '', 'firmalife:plant/%s_branch[up=true,down=true]' % fruit, 'firmalife:plant/%s_leaves[lifecycle=%s]' % (fruit, life)) for life in ('dormant', 'healthy', 'flowering', 'fruiting')])
    return left, right, page_break()

if __name__ == '__main__':
    main_with_args()

