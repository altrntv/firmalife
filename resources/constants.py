from typing import Dict, List, NamedTuple, Sequence, Optional, Literal, Tuple, Any, Set

OreGrade = NamedTuple('OreGrade', weight=int, grind_amount=int)
RockCategory = Literal['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']
Rock = NamedTuple('Rock', category=RockCategory, sand=str)
Fruit = NamedTuple('Fruit', min_temp=float, max_temp=float, min_rain=float, max_rain=float)

class Wood(NamedTuple):
    temp: float
    duration: int

SIMPLE_ITEMS = ('fruit_leaf', 'cinnamon_bark', 'beeswax', 'pineapple_fiber', 'pineapple_leather', 'pineapple_yarn', 'raw_honey', 'rennet', 'watering_can', 'treated_lumber', 'cheesecloth', 'spoon',
                'pie_pan', 'seed_ball', 'rustic_finish', 'stone_finish', 'tile_finish', 'oven_insulation', 'ice_shavings', 'beekeeper_helmet', 'beekeeper_chestplate', 'beekeeper_leggings', 'beekeeper_boots', 'reinforced_glass', 'pottery_sherd',
                'hematitic_wine_bottle', 'olivine_wine_bottle', 'volcanic_wine_bottle', 'empty_hematitic_wine_bottle', 'empty_olivine_wine_bottle', 'empty_volcanic_wine_bottle', 'cork', 'bottle_label', 'barrel_stave')
SIMPLE_FOODS = ('frothy_coconut', 'white_chocolate_blend', 'dark_chocolate_blend', 'milk_chocolate_blend', 'tofu', 'soy_mixture', 'yak_curd', 'goat_curd', 'milk_curd', 'cheddar', 'chevre', 'rajya_metok', 'gouda', 'feta', 'shosha', 'butter',
                'pie_dough', 'filled_pie', 'cooked_pie', 'pizza_dough', 'raw_pizza', 'cooked_pizza', 'shredded_cheese', 'pickled_egg', 'pumpkin_pie_dough', 'raw_pumpkin_pie', 'cooked_pumpkin_pie', 'cocoa_beans', 'roasted_cocoa_beans',
                'cocoa_butter', 'cocoa_powder', 'toast', 'dark_chocolate', 'milk_chocolate', 'white_chocolate', 'garlic_bread', 'cured_maize', 'nixtamal', 'masa', 'masa_flour', 'corn_tortilla', 'taco_shell', 'burrito', 'taco', 'salsa',
                'tomato_sauce', 'nightshade_berry', 'stinky_soup', 'toast_with_jam', 'toast_with_butter', 'bacon', 'cooked_bacon', 'tomato_sauce_mix', 'vanilla_ice_cream', 'strawberry_ice_cream', 'chocolate_ice_cream', 'banana_split',
                'cookie_dough', 'sugar_cookie', 'chocolate_chip_cookie_dough', 'chocolate_chip_cookie', 'cookie_dough_ice_cream', 'maki_roll', 'futo_maki_roll', 'hardtack', 'hardtack_dough', 'cooked_lasagna', 'cooked_pasta',
                'cooked_rice_noodles', 'nachos', 'pasta_with_tomato_sauce', 'raw_egg_noodles', 'raw_lasagna', 'raw_rice_noodles', 'tortilla_chips', 'smashed_red_grapes', 'smashed_white_grapes')
SIMPLE_SPICES = ('ground_cinnamon', 'cinnamon', 'basil_leaves', 'vanilla')
SIMPLE_BLOCKS: Dict[str, str] = {
    'sealed_bricks': 'minecraft:mineable/pickaxe',
    'chiseled_sealed_bricks': 'minecraft:mineable/pickaxe',
    'polished_sealed_bricks': 'minecraft:mineable/pickaxe',
    'treated_wood': 'minecraft:mineable/axe',
    'tiles': 'minecraft:mineable/pickaxe',
    'rustic_bricks': 'minecraft:mineable/pickaxe',
}
COLORS = ('white', 'orange', 'magenta', 'light_blue', 'yellow', 'lime', 'pink', 'gray', 'light_gray', 'cyan', 'purple', 'blue', 'brown', 'green', 'red', 'black')
BLOCK_ENTITIES = ('oven_bottom', 'oven_top', 'drying_mat', 'beehive', 'solar_drier', 'mixing_bowl', 'string', 'berry_bush', 'large_planter', 'bonsai_planter', 'trellis_planter', 'hanging_planter', 'quad_planter', 'climate_station', 'hydroponic_planter', 'vat', 'oven_hopper', 'ashtray', 'stovetop_grill', 'stovetop_pot', 'jarbnet', 'plate', 'big_barrel', 'jarring_station', 'barrel_press', 'stomping_barrel')
EXTRA_FLUIDS = ('yeast_starter', 'coconut_milk', 'yak_milk', 'goat_milk', 'curdled_yak_milk', 'curdled_goat_milk', 'pina_colada', 'cream', 'chocolate', 'sugar_water', 'fruity_fluid', 'mead')
WINES = ('red', 'white', 'rose', 'sparkling', 'dessert')
JARS: Sequence[Tuple[str, int, str]] = (
    ('honey', 1, 'firmalife:raw_honey'),
    ('compost', 8, 'tfc:compost'),
    ('rotten_compost', 8, 'tfc:rotten_compost'),
    ('guano', 8, 'tfc:groundcover/guano'),
)
CHEESE_WHEELS = ('rajya_metok', 'cheddar', 'gouda', 'feta', 'chevre', 'shosha')
FL_FRUITS = ('fig', 'pineapple', 'red_grapes', 'white_grapes')
DEFAULT_FORGE_ORE_TAGS: Tuple[str, ...] = ('coal', 'diamond', 'emerald', 'gold', 'iron', 'lapis', 'netherite_scrap', 'quartz', 'redstone')
STILL_BUSHES = {
    'nightshade': (200, 400, 7, 24),
    'pineapple': (250, 500, 20, 32),
}
ARMOR_SECTIONS = ('helmet', 'chestplate', 'leggings', 'boots')

TFC_GRAINS = ('wheat', 'rye', 'barley', 'rice', 'maize', 'oat')
TFC_FRUIT_TREES = ('cherry', 'green_apple', 'lemon', 'olive', 'orange', 'peach', 'plum', 'red_apple')
TFC_FRUITS = ('banana', 'blackberry', 'blueberry', 'bunchberry', 'cherry', 'cloudberry', 'cranberry', 'elderberry', 'gooseberry', 'green_apple', 'lemon', 'melon_slice', 'olive', 'orange', 'peach', 'plum', 'pumpkin_chunks', 'raspberry', 'red_apple', 'snowberry', 'strawberry', 'wintergreen_berry')
TFC_FLOWERS = ('canna', 'goldenrod', 'allium', 'anthurium', 'houstonia', 'blood_lily', 'blue_orchid', 'blue_ginger', 'butterfly_milkweed', 'black_orchid',
               'dandelion', 'desert_flame', 'field_horsetail', 'grape_hyacinth', 'heliconia', 'kangaroo_paw', 'labrador_tea', 'lady_fern', 'calendula',
               'meads_milkweed', 'nasturtium', 'oxeye_daisy', 'poppy', 'primrose', 'pulsatilla', 'sacred_datura', 'silver_spurflower', 'snapdragon_pink',
               'snapdragon_red', 'snapdragon_white', 'snapdragon_yellow', 'strelitzia', 'sword_fern', 'trillium', 'tropical_milkweed', 'tulip_orange', 'tulip_pink',
               'tulip_red', 'tulip_white')
TFC_FISH_ITEMS = ('cod', 'bluegill', 'crappie', 'lake_trout', 'largemouth_bass', 'smallmouth_bass', 'rainbow_trout', 'salmon',)
TFC_FLOATING_FLOWERS = ('duckweed', 'lotus', 'pistia', 'water_canna', 'water_lily')
TFC_ROCKS: Dict[str, Rock] = {
    'granite': Rock('igneous_intrusive', 'white'),
    'diorite': Rock('igneous_intrusive', 'white'),
    'gabbro': Rock('igneous_intrusive', 'black'),
    'shale': Rock('sedimentary', 'black'),
    'claystone': Rock('sedimentary', 'brown'),
    'limestone': Rock('sedimentary', 'white'),
    'conglomerate': Rock('sedimentary', 'green'),
    'dolomite': Rock('sedimentary', 'black'),
    'chert': Rock('sedimentary', 'yellow'),
    'chalk': Rock('sedimentary', 'white'),
    'rhyolite': Rock('igneous_extrusive', 'red'),
    'basalt': Rock('igneous_extrusive', 'red'),
    'andesite': Rock('igneous_extrusive', 'red'),
    'dacite': Rock('igneous_extrusive', 'red'),
    'quartzite': Rock('metamorphic', 'white'),
    'slate': Rock('metamorphic', 'brown'),
    'phyllite': Rock('metamorphic', 'brown'),
    'schist': Rock('metamorphic', 'green'),
    'gneiss': Rock('metamorphic', 'green'),
    'marble': Rock('metamorphic', 'yellow')
}
TFC_WOODS: Dict[str, Wood] = {
    'acacia': Wood(650, 1000),
    'ash': Wood(696, 1250),
    'aspen': Wood(611, 1000),
    'birch': Wood(652, 1750),
    'blackwood': Wood(720, 1750),
    'chestnut': Wood(651, 1500),
    'douglas_fir': Wood(707, 1500),
    'hickory': Wood(762, 2000),
    'kapok': Wood(645, 1000),
    'mangrove': Wood(655, 1000),
    'maple': Wood(745, 2000),
    'oak': Wood(728, 2250),
    'palm': Wood(730, 1250),
    'pine': Wood(627, 1250),
    'rosewood': Wood(640, 1500),
    'sequoia': Wood(612, 1750),
    'spruce': Wood(608, 1500),
    'sycamore': Wood(653, 1750),
    'white_cedar': Wood(625, 1500),
    'willow': Wood(603, 1000)
}

ROCKS: Dict[str, Rock] = {
    'granite': Rock('igneous_intrusive', 'white'),
    'diorite': Rock('igneous_intrusive', 'white'),
    'gabbro': Rock('igneous_intrusive', 'black'),
    'shale': Rock('sedimentary', 'black'),
    'claystone': Rock('sedimentary', 'brown'),
    'limestone': Rock('sedimentary', 'white'),
    'conglomerate': Rock('sedimentary', 'green'),
    'dolomite': Rock('sedimentary', 'black'),
    'chert': Rock('sedimentary', 'yellow'),
    'chalk': Rock('sedimentary', 'white'),
    'rhyolite': Rock('igneous_extrusive', 'red'),
    'basalt': Rock('igneous_extrusive', 'red'),
    'andesite': Rock('igneous_extrusive', 'red'),
    'dacite': Rock('igneous_extrusive', 'yellow'),
    'quartzite': Rock('metamorphic', 'white'),
    'slate': Rock('metamorphic', 'yellow'),
    'phyllite': Rock('metamorphic', 'brown'),
    'schist': Rock('metamorphic', 'green'),
    'gneiss': Rock('metamorphic', 'green'),
    'marble': Rock('metamorphic', 'yellow')
}

ROCK_CATEGORIES: List[str] = ['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']
ORE_GRADES: Dict[str, OreGrade] = {
    'normal': OreGrade(50, 5),
    'poor': OreGrade(30, 3),
    'rich': OreGrade(20, 7)
}

GREENHOUSES = ('rusted_iron', 'iron', 'oxidized_copper', 'weathered_copper', 'exposed_copper', 'copper', 'weathered_treated_wood', 'treated_wood', 'stainless_steel')
GREENHOUSE_BLOCKS = ('roof', 'roof_top', 'wall', 'door')
CLEANING_PAIRS: Dict[str, str] = {
    'rusted_iron': 'iron',
    'oxidized_copper': 'copper',
    'weathered_copper': 'copper',
    'exposed_copper': 'copper',
    'weathered_treated_wood': 'treated_wood'
}
PLANTERS = ('hanging', 'bonsai', 'quad', 'large', 'trellis', 'hydroponic')

CARVINGS = {
    'none': ['XXXXX', 'XXXXX', 'XXXXX', 'X   X', 'XXXXX'],
    'circle': ['XXXXX', 'X   X', 'X   X', 'X   X', 'XXXXX'],
    'creeper': ['XXXXX', 'X X X', 'XX XX', 'X X X', 'X X X'],
    'axe': ['XXXXX', 'X  XX', 'X   X', 'X  XX', 'XXXXX'],
    'hammer': ['XXXXX', 'X   X', 'X   X', 'XX XX', 'XX XX'],
    'pickaxe': ['XXXXX', 'XX XX', 'X X X', 'XX XX', 'XX XX'],
    'left': ['XXXXX', 'X XXX', 'X XXX', 'X   X', 'XXXXX'],
    'right': ['XXXXX', 'X   X', 'XXX X', 'XXX X', 'XXXXX'],
}

HERBS = ('basil', 'bay_laurel', 'cardamom', 'cilantro', 'cumin', 'oregano', 'pimento', 'vanilla')

FRUITS: Dict[str, Fruit] = {
    'cocoa': Fruit(20, 35, 220, 400),
    'fig': Fruit(20, 35, 125, 215)
}

DISABLED_TFC_RECIPES = ('barrel/curdling', 'barrel/cheese', 'barrel/milk_vinegar', 'crafting/pumpkin_pie')

GENETIC_DISEASES = ['Malformed Rectum', 'Malphigian Tubule Iridescence', 'Rectal Stones', 'Poor Osmoregulation', 'Nosemosis', 'Broken Wings']
PARASITIC_INFECTIONS = ['Chalkbrood', 'Stonebrood', 'Foulbrood', 'Wax Moths', 'Hive Beetles', 'Mites']

DEFAULT_LANG = {
    'firmalife.creative_tab.firmalife': 'Firmalife Items',
    'effect.firmalife.swarm': 'Swarm',
    'entity.firmalife.seed_ball': 'Seed Ball',
    'entity.firmalife.bee': 'Bee',
    'firmalife.tooltip.food_trait.dried': 'Dried',
    'firmalife.tooltip.food_trait.aged': 'Aged',
    'firmalife.tooltip.food_trait.vintage': 'Vintage',
    'firmalife.tooltip.food_trait.fresh': 'Fresh',
    'firmalife.tooltip.food_trait.oven_baked': 'Oven Baked',
    'firmalife.tooltip.food_trait.hung': 'Hung in a Cellar',
    'firmalife.tooltip.food_trait.hung_2': 'Hung in a Cellar II',
    'firmalife.tooltip.food_trait.hung_3': 'Hung in a Cellar III',
    'firmalife.tooltip.food_trait.shelved': 'On a Cellar Shelf',
    'firmalife.tooltip.food_trait.shelved_2': 'On a Cellar Shelf II',
    'firmalife.tooltip.food_trait.shelved_3': 'On a Cellar Shelf III',
    'firmalife.tooltip.food_trait.smoked': 'Smoked',
    'firmalife.tooltip.food_trait.rancid_smoked': 'Rancid Smoked',
    'firmalife.tooltip.food_trait.fermented': 'Fermented',
    'firmalife.tooltip.food_trait.bee_pollinated': 'Bee Pollinated',
    'firmalife.tooltip.food_trait.slope_grown': 'Slope Grown',
    'firmalife.tooltip.food_trait.dirt_grown': 'Dirt Grown',
    'firmalife.tooltip.food_trait.gravel_grown': 'Gravel Grown',
    'firmalife.tooltip.seed_ball': 'Throw me!',
    'firmalife.tooltip.seed_ball_disabled': 'This server has disabled seed balls!',
    'firmalife.tooltip.planter_usable': 'Plantable in a %s',
    'firmalife.tooltip.beekeeper_armor': 'Protects from bees if full suit is worn',
    'firmalife.tooltip.knapping.rotten': 'This item is rotten and cannot be knapped.',
    'firmalife.cellar.found': 'Found a cellar of %s blocks',
    'firmalife.cellar.valid_block': '§aIn a valid cellar',
    'firmalife.cellar.invalid_block': '§4Invalid cellar',
    'firmalife.greenhouse.valid_block': '§aGrowing',
    'firmalife.greenhouse.invalid_block': '§4Not Growing',
    'firmalife.greenhouse.valid_generic': '§aIn a valid greenhouse',
    'firmalife.greenhouse.invalid_generic': '§cInvalid greenhouse',
    'firmalife.greenhouse.valid_sprinkler': '§aSprinkling',
    'firmalife.greenhouse.invalid_sprinkler': '§cNot sprinkling',
    'firmalife.greenhouse.no_sky': 'There is not enough sky light to grow',
    'firmalife.greenhouse.climate_invalid': 'This block is not in a valid greenhouse. Try clicking your climate station.',
    'firmalife.greenhouse.air_needed': 'Planters need a block of air to grow into.',
    'firmalife.greenhouse.dehydrated': 'This planter needs to be watered with a Watering Can',
    'firmalife.greenhouse.wrong_tier': 'To grow this crop, upgrade to a better greenhouse',
    'firmalife.greenhouse.wrong_type': 'This crop does not grow in this planter. It grows in a ',
    'firmalife.greenhouse.found': 'Found a %s greenhouse of %s blocks',
    'firmalife.greenhouse.needs_pipe': 'Needs to be attached to a greenhouse pipe with flowing water.',
    'firmalife.greenhouse.expects': 'Expects %s greenhouse',
    'firmalife.cellar.expects': 'Expects cellar',
    'firmalife.planter.growth_water': 'Growth: %s, Water: %s',
    'firmalife.tumbler.almost_ready': 'Compost almost ready!',
    'firmalife.tumbler.rotten': 'Rotten!',
    'firmalife.tumbler.ready': 'Ready',
    'firmalife.tumbler.total': 'Total: %s / 32',
    'firmalife.tumbler.component_pct': '%s - %s',
    'firmalife.enum.additiontype.none': 'None',
    'firmalife.enum.additiontype.poison': 'Poison',
    'firmalife.enum.additiontype.green': 'Green',
    'firmalife.enum.additiontype.brown': 'Brown',
    'firmalife.enum.additiontype.fish': 'Fish',
    'firmalife.enum.additiontype.charcoal': 'Charcoal',
    'firmalife.enum.additiontype.bone': 'Bones',
    'firmalife.enum.additiontype.pottery': 'Pottery',
    'firmalife.enum.winetype.red': 'Red',
    'firmalife.enum.winetype.white': 'White',
    'firmalife.enum.winetype.rose': 'Rosé',
    'firmalife.enum.winetype.sparkling': 'Sparkling',
    'firmalife.enum.winetype.dessert': 'Dessert',

    'firmalife.bee.queen': 'Queen',
    'firmalife.bee.no_queen': 'No Queen',
    'firmalife.bee.may_scrape': 'Right click with a knife to scrape',
    'firmalife.bee.abilities': 'Abilities:',
    'firmalife.bee.ability.hardiness': 'Hardiness %s',
    'firmalife.bee.ability.production': 'Production %s',
    'firmalife.bee.ability.mutant': 'Mutant %s',
    'firmalife.bee.ability.fertility': 'Fertility %s',
    'firmalife.bee.ability.crop_affinity': 'Crop Affinity %s',
    'firmalife.bee.ability.nature_restoration': 'Nature Restoration %s',
    'firmalife.bee.ability.calmness': 'Calmness %s',
    'firmalife.bee.genetic_disease': 'Genetic Disease: %s',
    'firmalife.bee.parasitic_infection': 'Parasitic Infection: %s',
    **{'firmalife.bee.disease%s' % i: d for i, d in enumerate(GENETIC_DISEASES)},
    **{'firmalife.bee.infection%s' % i: d for i, d in enumerate(PARASITIC_INFECTIONS)},
    'firmalife.beehive.honey': 'Honey: %s / 12',
    'firmalife.beehive.bee': 'Frame %s: ',
    'firmalife.beehive.bee_cold': 'Too cold! %s C < %s C',
    'firmalife.beehive.has_queen': 'Has Queen. ',
    'firmalife.beehive.no_queen': 'Empty',
    'firmalife.beehive.flowers': 'Flowers: %s',
    'firmalife.beehive.min_flowers': 'Not enough flowers!',
    'firmalife.beehive.breed_chance': 'Daily New Bee Chance: 1 / %s',
    'firmalife.beehive.breed_chance_100': 'Daily New Bee Chance: Guaranteed',
    'firmalife.beehive.honey_chance': 'Daily Honey Chance: 1 / %s',
    'firmalife.beehive.honey_chance_100': 'Daily Honey Chance: Guaranteed',
    'firmalife.wine.age_time': 'Aging for: %s',
    'firmalife.wine.age_time_opened': 'Aged for: %s',
    'firmalife.wine.sealed': '%s Wine Bottle',
    'firmalife.wine.unsealed': 'Open %s Wine Bottle',
    'firmalife.wine.making': 'Making %s Wine',
    'firmalife.wine.how_to_open': 'Right click with a knife to pop the cork.',
    'firmalife.wine.has_output': '%s servings of %s wine',
    'firmalife.wine.need_cork': 'You need corks to bottle wine!',
    'firmalife.wine.empty': 'Empty (may be cleaned in barrel of water)',
    'greenhouse.firmalife.treated_wood': 'Treated Wood',
    'greenhouse.firmalife.copper': 'Copper',
    'greenhouse.firmalife.iron': 'Iron',
    'greenhouse.firmalife.stainless_steel': 'Stainless Steel',

    'firmalife.bowl.spoon': 'The bowl is missing a spoon.',
    'firmalife.bowl.mixing': 'The bowl is currently mixing',
    'firmalife.bowl.no_recipe': 'The bowl has no recipe.',
    'firmalife.bowl.matching_recipe': 'The bowl\'s contents do not match the recipe',

    'firmalife.enum.plantertype.hanging': 'Hanging Planter',
    'firmalife.enum.plantertype.trellis': 'Trellis Planter',
    'firmalife.enum.plantertype.bonsai': 'Bonsai Planter',
    'firmalife.enum.plantertype.quad': 'Quad Planter',
    'firmalife.enum.plantertype.large': 'Large Planter',
    'firmalife.enum.plantertype.hydroponic': 'Hydroponic Planter',

    'firmalife.enum.foodage.fresh': 'Fresh',
    'firmalife.enum.foodage.aged': 'Aged',
    'firmalife.enum.foodage.vintage': 'Vintage',

    'firmalife.jade.food_age': 'Age: %s',
    'firmalife.jade.aging': 'Currently Aging',
    'firmalife.jade.not_aging': 'Not Aging',
    'firmalife.jade.slices': 'Slices: %s',
    'firmalife.jade.cure_time_left': 'Curing Time Left: %s',
    'firmalife.jade.cannot_cure': 'Not hot enough to cure!',
    'firmalife.jade.cook_left': '%s: %s',
    'firmalife.jade.cook_left_temp': '%s: %s (%s)',
    'firmalife.jade.boiling': 'Boiling',
    'firmalife.jade.not_insulated': 'Not insulated!',
    'firmalife.jade.no_chimney': 'Missing chimney!',
    'firmalife.jade.needs_peel': 'Needs peel item to safely remove goods',
    'firmalife.jade.has_firepit': 'Has an eligible firepit',
    'firmalife.jade.no_firepit': 'No eligible firepit detected',

    'config.jade.plugin_firmalife.fruit_tree_sapling': 'Fruit Tree Sapling',
    'config.jade.plugin_firmalife.drying_mat': 'Drying Mat',
    'config.jade.plugin_firmalife.solar_drier': 'Solar Drier',
    'config.jade.plugin_firmalife.string': 'String',
    'config.jade.plugin_firmalife.cheese': 'Cheese',
    'config.jade.plugin_firmalife.oven_bottom': 'Oven Bottom',
    'config.jade.plugin_firmalife.oven_top': 'Oven Top',
    'config.jade.plugin_firmalife.shelf': 'Shelf',
    'config.jade.plugin_firmalife.hanger': 'Hanger',
    'config.jade.plugin_firmalife.vat': 'Vat',
    'config.jade.plugin_firmalife.tumbler': 'Tumbler',
    'config.jade.plugin_firmalife.jarbnet': 'Jarbnet',
    'config.jade.plugin_firmalife.barrel_press': 'Barrel Press',

    'death.attack.firmalife.oven': '%1$s died by sticking their hand in a hot oven.',
    'death.attack.firmalife.oven.player': '%1$s climbed into an oven to escape %2$s.',
    'death.attack.firmalife.swarm': '%1$s was stung to death by a swarm of bees!',
    'death.attack.firmalife.swarm.player': '%1$s was stung to death by a swarm of bees while trying to escape %2$s.',
    'subtitles.item.firmalife.hollow_shell.blow': 'Shell whistles',

    'item.firmalife.hollow_shell.filled': '%s Hollow Shell',
    'item.firmalife.wine_glass.filled': 'Glass of %s',
    'firmalife.screen.pumpkin_knapping': 'Pumpkin Knapping',
    'tfc.jei.pumpkin_knapping': 'Pumpkin Knapping',
    'tfc.jei.drying': 'Drying',
    'tfc.jei.smoking': 'Smoking',
    'tfc.jei.mixing_bowl': 'Mixing Bowl',
    'tfc.jei.oven': 'Oven',
    'tfc.jei.vat': 'Vat',
    'tfc.jei.stomping': 'Stomping',
    'tfc.jei.bowl_pot': 'Bowl Pot',
}

def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()
