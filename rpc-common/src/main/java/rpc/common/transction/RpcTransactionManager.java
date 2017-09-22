package rpc.common.transction;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;

public class RpcTransactionManager extends DataSourceTransactionManager {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = -498060848670059369L;

	@Override
	public void setDataSource(DataSource dataSource) {
		// TODO Auto-generated method stub
		super.setDataSource(dataSource);
	}

	@Override
	public DataSource getDataSource() {
		// TODO Auto-generated method stub
		return super.getDataSource();
	}

	@Override
	public void afterPropertiesSet() {
		// TODO Auto-generated method stub
		super.afterPropertiesSet();
	}

	@Override
	public Object getResourceFactory() {
		// TODO Auto-generated method stub
		return super.getResourceFactory();
	}

	@Override
	protected Object doGetTransaction() {
		// TODO Auto-generated method stub
		return super.doGetTransaction();
	}

	@Override
	protected boolean isExistingTransaction(Object transaction) {
		// TODO Auto-generated method stub
		return super.isExistingTransaction(transaction);
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		// TODO Auto-generated method stub
		super.doBegin(transaction, definition);
	}

	@Override
	protected Object doSuspend(Object transaction) {
		// TODO Auto-generated method stub
		return super.doSuspend(transaction);
	}

	@Override
	protected void doResume(Object transaction, Object suspendedResources) {
		// TODO Auto-generated method stub
		super.doResume(transaction, suspendedResources);
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		// TODO Auto-generated method stub
		super.doCommit(status);
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) {
		// TODO Auto-generated method stub
		super.doRollback(status);
	}

	@Override
	protected void doSetRollbackOnly(DefaultTransactionStatus status) {
		// TODO Auto-generated method stub
		super.doSetRollbackOnly(status);
	}

	@Override
	protected void doCleanupAfterCompletion(Object transaction) {
		// TODO Auto-generated method stub
		super.doCleanupAfterCompletion(transaction);
	}

	@Override
	protected DefaultTransactionStatus newTransactionStatus(TransactionDefinition definition, Object transaction,
			boolean newTransaction, boolean newSynchronization, boolean debug, Object suspendedResources) {
		// TODO Auto-generated method stub
		return super.newTransactionStatus(definition, transaction, newTransaction, newSynchronization, debug,
				suspendedResources);
	}

	@Override
	protected void prepareSynchronization(DefaultTransactionStatus status, TransactionDefinition definition) {
		// TODO Auto-generated method stub
		super.prepareSynchronization(status, definition);
	}

	@Override
	protected int determineTimeout(TransactionDefinition definition) {
		// TODO Auto-generated method stub
		return super.determineTimeout(definition);
	}

	@Override
	protected boolean useSavepointForNestedTransaction() {
		// TODO Auto-generated method stub
		return super.useSavepointForNestedTransaction();
	}

	@Override
	protected boolean shouldCommitOnGlobalRollbackOnly() {
		// TODO Auto-generated method stub
		return super.shouldCommitOnGlobalRollbackOnly();
	}

	@Override
	protected void prepareForCommit(DefaultTransactionStatus status) {
		// TODO Auto-generated method stub
		super.prepareForCommit(status);
	}

	@Override
	protected void registerAfterCompletionWithExistingTransaction(Object transaction,
			List<TransactionSynchronization> synchronizations) throws TransactionException {
		// TODO Auto-generated method stub
		super.registerAfterCompletionWithExistingTransaction(transaction, synchronizations);
	}

	
	
	
}
