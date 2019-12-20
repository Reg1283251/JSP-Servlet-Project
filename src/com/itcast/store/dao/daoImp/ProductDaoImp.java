package com.itcast.store.dao.daoImp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.itcast.store.dao.ProductDao;
import com.itcast.store.domain.Product;
import com.itcast.store.utils.JDBCUtils;
import com.itcast.store.utils.Textempty;

public class ProductDaoImp implements ProductDao {

	@Override
	//����������Ʒ��Ϣ
	public List<Product> findHots() throws Exception {
		String sql = "select *from product where pflag=0 and is_hot=1 order by pdate desc limit 0 , 9";
		QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());
		return runner.query(sql, new BeanListHandler<Product>(Product.class));
	}

	@Override
	//������Ʒ��Ʒ��Ϣ
	public List<Product> findNews() throws Exception {
		String sql = "select *from product where pflag=0 order by pdate desc limit 0 , 9";
		QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());
		return runner.query(sql, new BeanListHandler<Product>(Product.class));
	}

	@Override
	//ͨ��id����ĳ����Ʒ
	public Product findProductByPid(String pid) throws Exception {
		String sql = "select *from product where pid = ?";
		QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());
		return runner.query(sql, new BeanHandler<Product>(Product.class), pid);
	}

	@Override
	//����ĳ����Ʒ����Ϣ
	public int findTotalRecords(String cid) throws Exception {
		String sql = "select count(*) from product where cid = ? and pflag=0";
		QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());
		Long num = (Long)runner.query(sql, new ScalarHandler(), cid);
		return num.intValue();
	}

	@Override
	//ͨ��ҳ�����Ҹ�ҳ����Ʒ��Ϣ
	public List findProductByCidWithPage(String cid, int startIndex, int pageSize) throws Exception {
		String sql = "select *from product where cid=? and pflag=0 limit ?, ?";
		QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());
		return runner.query(sql, new BeanListHandler<Product>(Product.class), cid, startIndex, pageSize);
	}

	@Override
	//������Ʒ �ϼ� �ܹ�����Ϣ��¼����
	public int findTotalRecords() throws Exception {
		String sql="select count(*) from product where pflag=0";
		QueryRunner qr=new QueryRunner(JDBCUtils.getDataSource());
		Long num=(Long)qr.query(sql, new ScalarHandler());
		return num.intValue();
	}
	//ͨ����ҳ�����ϼ���Ʒ��Ϣ
	public List<Product> findAllUpProductsWithPage(int startIndex, int pageSize) throws Exception{
		String sql="SELECT * FROM product WHERE pflag=0 ORDER BY pdate DESC LIMIT ?, ?";
		QueryRunner qr=new QueryRunner(JDBCUtils.getDataSource());
		return qr.query(sql, new BeanListHandler<Product>(Product.class),startIndex,pageSize);
	}

	@Override
	//������Ʒ��Ϣ
	public void saveProduct(Product product) throws Exception {
		String sql="INSERT INTO product VALUES(?,?,?,?,?,?,?,?,?,?)";
		QueryRunner qr=new QueryRunner(JDBCUtils.getDataSource());
		Object[] params={product.getPid(),product.getPname(),product.getMarket_price(),product.getShop_price(),product.getPimage(),product.getPdate(),product.getIs_hot(),product.getPdesc(),product.getPflag(),product.getCid()};
		qr.update(sql,params);
	}

	@Override
	//ͨ��id������Ʒ
	public Product getProductById(String pid) throws Exception {
		String sql = "select * from product where pid=?";
		QueryRunner qr=new QueryRunner(JDBCUtils.getDataSource());
		return qr.query(sql, new BeanHandler<Product>(Product.class),pid);
	}

	@Override
	//��Ʒ�¼ܡ��ϼ�
	public void Down_UP_Product(String statu, String pid) throws Exception {
		String sql = "update product set pflag = ? where pid=?";
		QueryRunner qr=new QueryRunner(JDBCUtils.getDataSource());
		qr.update(sql,statu, pid);
	}
	//�޸���Ʒ��Ϣ
	public void editProduct(Product product) throws Exception {
		String sql = "update product set pname=?, market_price=?, shop_price=?, pimage=?, pdate=?, is_hot=?"
				+ ", pdesc=?, pflag=?, cid=? where pid=?";
		Object[] params={product.getPname(),product.getMarket_price(),product.getShop_price(),product.getPimage(),product.getPdate(),product.getIs_hot(),product.getPdesc(),
				product.getPflag(),product.getCid(),product.getPid()};
		QueryRunner qr=new QueryRunner(JDBCUtils.getDataSource());
		qr.update(sql,params);
	}

	@Override
	//ͳ���¼���Ʒ����
	public int findDownTotalRecords() throws Exception {
		String sql="select count(*) from product where pflag=1";
		QueryRunner qr=new QueryRunner(JDBCUtils.getDataSource());
		Long num=(Long)qr.query(sql, new ScalarHandler());
		return num.intValue();
	}

	@Override
	//��ѯ�¼ܵ���Ʒ
	public List<Product> findPushDownProduct(int startIndex, int pageSize) throws Exception {
		String sql="SELECT * FROM product WHERE pflag=1 ORDER BY pdate DESC LIMIT ?, ?";
		QueryRunner qr=new QueryRunner(JDBCUtils.getDataSource());
		return qr.query(sql, new BeanListHandler<Product>(Product.class),startIndex,pageSize);
	}

	@Override
	//ȫ��������Ʒ
	public List<Product> searchProducts(String result) throws Exception {
		String sql = "SELECT *FROM product WHERE pflag=0 AND pname LIKE '%"+result+"%'";
		QueryRunner qr=new QueryRunner(JDBCUtils.getDataSource());
		return qr.query(sql, new BeanListHandler<Product>(Product.class));
	}

	@Override
	//����ĳ����Ʒ������
	public int findByCategoryTotalRecords(String cid) throws Exception {
		String sql = "select count(*) from product where cid=?";
		QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());
		Long num = (Long)runner.query(sql, new ScalarHandler(), cid);
		return num.intValue();
	}

	@Override
	//����ĳ����Ʒ��������Ʒ
	public List<Product> findProductsByCategory(String cid, int startIndex, int pageSize) throws Exception {
		String sql = "select *from product where cid=? limit ?, ?";
		QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());
		return runner.query(sql, new BeanListHandler<Product>(Product.class), cid, startIndex, pageSize);
	}

	@Override
	//ͨ����Ʒ������Ʒ�����Ʒ�Ƿ��ϼܡ���Ʒ�Ƿ�������
	public List<Product> searchProduct(String pname, String cid, String pflag, String is_hot) throws Exception {
		QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());
		String sql = "select * from product where 1=1";
		List<Object> list = new ArrayList<Object>();
		if(!Textempty.isEmpty(pname)) {  //ͨ����Ʒ����ѯ
			sql = sql + " and pname like ?";
			list.add("%" + pname + "%");
		}
		if(!Textempty.isEmpty(cid)) { //ͨ����Ʒ����ѯ
			sql = sql + " and cid=?";
			list.add(cid);
		}
		if(!Textempty.isEmpty(pflag)) {  //ͨ����Ʒ�Ƿ��ϼܲ�ѯ
			int flag = Integer.parseInt(pflag);
			sql = sql + " and pflag=?";
			list.add(flag);
		}
		if(!Textempty.isEmpty(is_hot)) { //ͨ����Ʒ�Ƿ�����
			int hot = Integer.parseInt(is_hot);
			sql = sql + " and is_hot=?";
			list.add(hot);
		}
		return runner.query(sql, new BeanListHandler<Product>(Product.class), list.toArray());
	}
}
