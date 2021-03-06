package com.sunbeam.sh4.hib3;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;

public class BookDao implements AutoCloseable {
	private Session session;
	public void open() {
		session = HbUtil.getSessionFactory().openSession();
	}
	public void close() {
		if(session!=null)
			session.close();
	}
	public Book getBook(int id) {
		Book b = session.get(Book.class, id);
		return b;
	}
	public void addBook(Book b) {
		Transaction tx = session.beginTransaction();
		try {
			session.persist(b);
			//session.flush();
			tx.commit();
		}catch (Exception e) {
			tx.rollback();
		}
	}
	public void updateBook(Book b) {
		Transaction tx = session.beginTransaction();
		try {
			session.update(b);
			//session.flush();
			tx.commit();
		}catch (Exception e) {
			tx.rollback();
		}
	}
	public void deleteBook(int id) {
		Transaction tx = session.beginTransaction();
		try {
			Book b = session.get(Book.class, id);
			if(b!=null)
				session.delete(b);
			//session.flush();
			tx.commit();
		}catch (Exception e) {
			tx.rollback();
		}
	}

//	public List<Book> getBooksOfSubject(String subject) {
//		//Criteria cr = session.createCriteria(Book.class);
//		DetachedCriteria dcr = DetachedCriteria.forClass(Book.class);
//		dcr.add(Restrictions.eq("subject", subject));
//		Criteria cr = dcr.getExecutableCriteria(session);
//		return cr.list();
//	}
	
	public List<Book> getBooksOfSubject(String subject) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Book> query = builder.createQuery(Book.class);
		Root<Book> root = query.from(Book.class);
		query.select(root);
		query.where(builder.equal(root.get("subject"), subject));
		Query<Book> q = session.createQuery(query);
		return q.getResultList();
	}
	
	public List<Book> getAllBooks() {
		DetachedCriteria dcr = DetachedCriteria.forClass(Book.class);
		Criteria cr = dcr.getExecutableCriteria(session);
		return cr.list();
	}
	
	public List<Book> getBooksOfSubjectAndAuthor(String subject, String author) {
		DetachedCriteria dcr = DetachedCriteria.forClass(Book.class);
		dcr.add(Restrictions.eq("subject", subject));
		dcr.add(Restrictions.eq("author", author));
		Criteria cr = dcr.getExecutableCriteria(session);
		return cr.list();
	}
	
	public List<Book> getBooksOfSubjectOrAuthor(String subject, String author) {
		DetachedCriteria dcr = DetachedCriteria.forClass(Book.class);
		dcr.add(
			Restrictions.or(
					Restrictions.eq("subject", subject), 
					Restrictions.eq("author", author)
				)
			);
		dcr.addOrder(Order.desc("id"));
		Criteria cr = dcr.getExecutableCriteria(session);
		return cr.list();
	}
	
	public List<String> getSubjects() {
		String sql = "SELECT DISTINCT SUBJECT FROM BOOKS";
		NativeQuery q = session.createSQLQuery(sql);
		return q.list();
	}
	
	public List<Book> hqlGetAllBooks() {
		String hql = "from Book b";
		Query<Book> q = session.createQuery(hql);
		return q.getResultList();
	}
	
	public List<Book> hqlGetBooksOfSubject(String subject) {
		String hql = "from Book b where b.subject=:p_subject";
		Query<Book> q = session.createQuery(hql);
		//q.setString("p_subject", subject);
		q.setParameter("p_subject", subject);
		return q.getResultList();
	}
	
	public List<Book> hqlGetBooksOfAuthor(String author) {
		Query<Book> q = session.getNamedQuery("hqlGetBooksOfAuthor");
		//q.setString("p_au", author);
		q.setParameter("p_au", author);
		return q.getResultList();
	}
	
	public List<Object[]> hqlSelectColumns() {
		String hql = "select b.id, b.name, b.price from Book b order by b.price desc";
		Query<Object[]> q = session.createQuery(hql);
		return q.getResultList();
	}
	
	public List<Book> hqlSelectColumnsTransformResult() {
		String hql = "select b.id, b.name, b.price from Book b order by b.price desc";
		Query<Book> q = session.createQuery(hql);
		q.setResultTransformer(new ResultTransformer() {
			@Override
			public Object transformTuple(Object[] result, String[] aliases) {
				Book b = new Book();
				b.setId((Integer)result[0]);
				b.setName((String)result[1]);
				b.setPrice((Double)result[2]);
				return b;
			}
			@Override
			public List transformList(List list) {
				return list;
			}
		});
		return q.getResultList();
	}
	
	public List<Book> hqlSelectColumnsInObject() {
		String hql = "select new Book(b.id, b.name, b.price) from Book b order by b.price desc";
		Query<Book> q = session.createQuery(hql);
		return q.getResultList();
	}	
	
	public void hqlDeleteBooksOfSubject(String subject) {
		String hql = "delete b from Book b where b.subject=:p_subject";
		Query<Book> q = session.createQuery(hql);
		q.setParameter("p_subject", subject);
		q.executeUpdate();
	}
}




