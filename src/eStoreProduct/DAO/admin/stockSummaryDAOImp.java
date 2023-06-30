package eStoreProduct.DAO.admin;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eStoreProduct.model.admin.entities.productStockModel;
import eStoreProduct.model.admin.entities.productsModel;
import eStoreProduct.model.admin.output.stockSummaryModel;

@Component
public class stockSummaryDAOImp implements stockSummaryDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public List<stockSummaryModel> getStocks() {
		String query = "SELECT NEW eStoreProduct.model.admin.output.stockSummaryModel("
				+ "p.id, p.title, p.productCategory, p.hsnCode, p.brand, p.imageUrl, p.description, p.reorderLevel, pc.prct_title, "
				+ "hsn.sgst, hsn.igst, hsn.cgst, hsn.gst, ps.price, ps.stock, ps.mrp) "
				+ "FROM eStoreProduct.model.admin.entities.productsModel p, eStoreProduct.model.admin.entities.productCategoryModel pc, eStoreProduct.model.admin.entities.HSNCodeModel hsn, eStoreProduct.model.admin.entities.productStockModel ps "
				+ "WHERE p.id = ps.product AND p.hsnCode = hsn.hsnCode AND pc.id = p.productCategory";

		try {
			int count = 0;
			List<stockSummaryModel> l = entityManager.createQuery(query, stockSummaryModel.class).getResultList();
			for (stockSummaryModel lis : l)
				count += 1;
			totalStocks(count);
			return l;
		} catch (Exception e) {
			// Handle the exception appropriately (e.g., logging, throwing custom exception, etc.)
			e.printStackTrace();
			return Collections.emptyList(); // or throw an exception if required
		}
	}

	@Override
	public int totalStocks(int count) {
		return count;
	}

	@Override
	@Transactional
	public long getTotalStocks() {
		String query = "SELECT COUNT(*) FROM eStoreProduct.model.admin.entities.productsModel";
		// Update the query to count the rows from the appropriate entity table

		try {
			TypedQuery<Long> countQuery = entityManager.createQuery(query, Long.class);
			return countQuery.getSingleResult();
		} catch (Exception e) {
			// Handle the exception appropriately (e.g., logging, throwing custom exception, etc.)
			e.printStackTrace();
			return 0; // or throw an exception if required
		}
	}

	@Override
	@Transactional
	public List<stockSummaryModel> getStocks(int page, int pageSize) {
		String query = "SELECT NEW eStoreProduct.model.admin.output.stockSummaryModel("
				+ "p.id, p.title, p.productCategory, p.hsnCode, p.brand, p.imageUrl, p.description, p.reorderLevel, pc.prct_title, "
				+ "hsn.sgst, hsn.igst, hsn.cgst, hsn.gst, ps.price, ps.stock, ps.mrp) "
				+ "FROM eStoreProduct.model.admin.entities.productsModel p, eStoreProduct.model.admin.entities.productCategoryModel pc, eStoreProduct.model.admin.entities.HSNCodeModel hsn, eStoreProduct.model.admin.entities.productStockModel ps "
				+ "WHERE p.id = ps.product AND p.hsnCode = hsn.hsnCode AND pc.id = p.productCategory";

		try {
			TypedQuery<stockSummaryModel> typedQuery = entityManager.createQuery(query, stockSummaryModel.class);
			typedQuery.setFirstResult(page * pageSize);
			typedQuery.setMaxResults(pageSize);
			List<stockSummaryModel> resultList = typedQuery.getResultList();
			for (stockSummaryModel lis : resultList) {
				System.out.println("Inside: " + lis);
			}
			return resultList;
		} catch (Exception e) {
			// Handle the exception appropriately (e.g., logging, throwing custom exception, etc.)
			e.printStackTrace();
			return Collections.emptyList(); // or throw an exception if required
		}
	}

	@Override
	@Transactional
	public void updateStocks(int prodid, String imageurl, String desc, int reorderlevel, int stock, double mrp,
			double price) {
		System.out.println("In the method: " + prodid + "stock" + stock);

		try {
			productsModel product = entityManager.find(productsModel.class, prodid);
			// HSNCodeModel hsn = entityManager.find(HSNCodeModel.class, gstcid);

			// productCategoryModel pc=entityManager.find(productCategoryModel.class, gstcid);

			// double gstPercent = hsn.getGst();
			// double updated_mrp = ((mrp * gstPercent * 0.01) + mrp);
			productStockModel psm = entityManager.find(productStockModel.class, prodid);

			if (product != null) {
				product.setReorderLevel(reorderlevel);
				product.setImageUrl(imageurl);
				product.setDescription(desc);
				// product.setHsnCode(gstcid);
				entityManager.merge(product);
				System.out.println("Product updated successfully.");

			} else {
				System.out.println("Product not found.");
			}
			if (psm != null) {
				psm.setStock(stock);
				// psm.setMrp(mrp);
				psm.setPrice(price);
				entityManager.merge(psm);
				System.out.println("stock updated successfully.");

			} else {
				System.out.println("Stock not found.");
			}
		} catch (Exception e) {
			System.out.println("Error updating the product: " + e.getMessage());
			e.printStackTrace();
		}

	}

}