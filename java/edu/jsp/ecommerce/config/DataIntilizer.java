package edu.jsp.ecommerce.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import edu.jsp.ecommerce.entity.Brand;
import edu.jsp.ecommerce.entity.Category;
import edu.jsp.ecommerce.entity.RoleEntity;
import edu.jsp.ecommerce.enums.Role;
import edu.jsp.ecommerce.repository.BrandRepository;
import edu.jsp.ecommerce.repository.CategoryRepository;
import edu.jsp.ecommerce.repository.RoleEntityRepository;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class DataIntilizer implements CommandLineRunner {

	private RoleEntityRepository roleEntityRepository;
	private BrandRepository brandRepository;
	private CategoryRepository categoryRepository;

	@Override
	public void run(String... args) throws Exception {
		seedRoles();
		seedBrand();
		seedCategorys();
	}

	public void seedRoles() {

		Role roles[] = Role.values();

		Arrays.stream(roles).forEach(r -> {
			if (roleEntityRepository.findByName(r).isEmpty()) {
				roleEntityRepository.save(new RoleEntity(r));
			}
		});

	}
	
	public void  seedBrand() {
		if(brandRepository.count() > 0) {
			return ;
		}
		List<Brand> brands = List.of(
	            brand("Samsung",  "https://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Samsung_Logo.svg/200px-Samsung_Logo.svg.png"),
	            brand("Apple",    "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Apple_logo_black.svg/100px-Apple_logo_black.svg.png"),
	            brand("Nike",     "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a6/Logo_NIKE.svg/200px-Logo_NIKE.svg.png"),
	            brand("Adidas",   "https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Adidas_Logo.svg/200px-Adidas_Logo.svg.png"),
	            brand("Sony",     "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Sony_logo.svg/200px-Sony_logo.svg.png"),
	            brand("LG",       "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bf/LG_logo_%282015%29.svg/200px-LG_logo_%282015%29.svg.png"),
	            brand("OnePlus",  null),
	            brand("Boat",     null),
	            brand("Puma",     "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Puma_Logo_Brand_2.png/200px-Puma_Logo_Brand_2.png"),
	            brand("H&M",      null)
	        );
		
		brandRepository.saveAll(brands);
	}
	private   Brand  brand(String name,String logoUrl) {
	
		return  Brand.builder().name(name).logoUrl(logoUrl).active(true).build();
	}
	
	
	public  void  seedCategorys() {
		
		if(categoryRepository.count() > 0) {
			return;
		}

        Category electronics = save(cat("Electronics",   "Consumer electronics & gadgets",   null, "https://img.icons8.com/color/96/electronics.png"));
        Category fashion     = save(cat("Fashion",       "Clothing, footwear & accessories",  null, "https://img.icons8.com/color/96/shopping-bag.png"));
        Category home        = save(cat("Home & Kitchen","Home decor, kitchen & appliances",  null, "https://img.icons8.com/color/96/home.png"));
        Category sports      = save(cat("Sports",        "Sports, fitness & outdoors",        null, "https://img.icons8.com/color/96/sports.png"));
        Category books       = save(cat("Books",         "Books, e-books & stationery",       null, "https://img.icons8.com/color/96/book.png"));
        Category beauty      = save(cat("Beauty",        "Beauty, health & personal care",    null, "https://img.icons8.com/color/96/makeup.png"));
        Category toys        = save(cat("Toys & Baby",   "Toys, games & baby products",       null, "https://img.icons8.com/color/96/teddy-bear.png"));

        save(cat("Mobiles",           "Smartphones & accessories",        electronics, null));
        save(cat("Laptops",           "Laptops, tablets & accessories",   electronics, null));
        save(cat("Cameras",           "DSLR, mirrorless & accessories",   electronics, null));
        save(cat("Audio",             "Headphones, earbuds & speakers",   electronics, null));
        save(cat("Televisions",       "Smart TVs & accessories",          electronics, null));
        save(cat("Wearables",         "Smartwatches & fitness bands",     electronics, null));
        save(cat("Men's Clothing",    "Shirts, trousers & ethnic wear",   fashion,     null));
        save(cat("Women's Clothing",  "Kurtas, sarees & western wear",    fashion,     null));
        save(cat("Footwear",          "Shoes, sandals & sneakers",        fashion,     null));
        save(cat("Accessories",       "Watches, bags & sunglasses",       fashion,     null));
        save(cat("Kitchen Appliances","Mixers, OTGs & microwaves",        home,        null));
        save(cat("Furniture",         "Sofas, beds & storage", 	           home,        null));
        save(cat("Decor",             "Lamps, clocks & art",              home,        null));
        save(cat("Gym & Fitness",     "Dumbbells, mats & equipment",      sports,      null));
        save(cat("Cricket",           "Bats, balls & gear",               sports,      null));
        save(cat("Cycling",           "Cycles, helmets & accessories",    sports,      null));

		
	}
	
	
	
	
	
	
	
	
	
	
	private  Category   cat(String name,String description,Category parent,String imageUrl) {
		return Category.builder().name(name)
				.description(description)
				.parent(parent)
				.imageUrl(imageUrl)
				.active(true)
				.build();
	}
	
	
	
	private   Category   save(Category category) {
		 return categoryRepository.save(category);
	}
	
	
	
	
	
	
	
}
