/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.metamodel.spi.binding;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.hibernate.AssertionFailure;
import org.hibernate.metamodel.spi.domain.PluralAttribute;
import org.hibernate.metamodel.spi.relational.Table;
import org.hibernate.metamodel.spi.relational.TableSpecification;
import org.hibernate.persister.collection.CollectionPersister;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public abstract class AbstractPluralAttributeBinding extends AbstractAttributeBinding implements PluralAttributeBinding {
	private final PluralAttributeKeyBinding pluralAttributeKeyBinding;
	private final AbstractPluralAttributeElementBinding pluralAttributeElementBinding;

	private Table collectionTable;

	private int batchSize = -1;

	private Caching caching;

	private boolean inverse;
	private boolean mutable = true;

	private Class<? extends CollectionPersister> collectionPersisterClass;

	private String where;
	private String orderBy;
	private boolean sorted;
	private Comparator comparator;
	private String comparatorClassName;

	private String customLoaderName;
	private CustomSQL customSqlInsert;
	private CustomSQL customSqlUpdate;
	private CustomSQL customSqlDelete;
	private CustomSQL customSqlDeleteAll;

	private String referencedPropertyName;

	private final java.util.Map filters = new HashMap();
	private final java.util.Set<String> synchronizedTables = new HashSet<String>();

	protected AbstractPluralAttributeBinding(
			AttributeBindingContainer container,
			PluralAttribute attribute,
			PluralAttributeElementNature pluralAttributeElementNature) {
		super( container, attribute );
		this.pluralAttributeKeyBinding = new PluralAttributeKeyBinding( this );
		this.pluralAttributeElementBinding = interpretNature( pluralAttributeElementNature );
	}

	private AbstractPluralAttributeElementBinding interpretNature(PluralAttributeElementNature pluralAttributeElementNature) {
		switch ( pluralAttributeElementNature ) {
			case BASIC: {
				return new BasicPluralAttributeElementBinding( this );
			}
			case COMPOSITE: {
				return new CompositePluralAttributeElementBinding( this );
			}
			case ONE_TO_MANY: {
				return new OneToManyPluralAttributeElementBinding( this );
			}
			case MANY_TO_MANY: {
				return new ManyToManyPluralAttributeElementBinding( this );
			}
			case MANY_TO_ANY: {
				return new ManyToAnyPluralAttributeElementBinding( this );
			}
			default: {
				throw new AssertionFailure( "Unknown collection element nature : " + pluralAttributeElementNature );
			}
		}
	}

//	protected void initializeBinding(PluralAttributeBindingState state) {
//		super.initialize( state );
//		fetchMode = state.getFetchMode();
//		extraLazy = state.isExtraLazy();
//		pluralAttributeElementBinding.setNodeName( state.getElementNodeName() );
//		pluralAttributeElementBinding.setTypeName( state.getElementTypeName() );
//		inverse = state.isInverse();
//		mutable = state.isMutable();
//		subselectLoadable = state.isSubselectLoadable();
//		if ( isSubselectLoadable() ) {
//			getEntityBinding().setSubselectLoadableCollections( true );
//		}
//		cacheConcurrencyStrategy = state.getCacheConcurrencyStrategy();
//		cacheRegionName = state.getCacheRegionName();
//		orderBy = state.getOrderBy();
//		where = state.getWhere();
//		referencedPropertyName = state.getReferencedPropertyName();
//		sorted = state.isSorted();
//		comparator = state.getComparator();
//		comparatorClassName = state.getComparatorClassName();
//		orphanDelete = state.isOrphanDelete();
//		batchSize = state.getBatchSize();
//		embedded = state.isEmbedded();
//		optimisticLocked = state.isOptimisticLocked();
//		collectionPersisterClass = state.getCollectionPersisterClass();
//		filters.putAll( state.getFilters() );
//		synchronizedTables.addAll( state.getSynchronizedTables() );
//		customSQLInsert = state.getCustomSQLInsert();
//		customSQLUpdate = state.getCustomSQLUpdate();
//		customSQLDelete = state.getCustomSQLDelete();
//		customSQLDeleteAll = state.getCustomSQLDeleteAll();
//		loaderName = state.getLoaderName();
//	}

	@Override
	public PluralAttribute getAttribute() {
		return (PluralAttribute) super.getAttribute();
	}

	@Override
	public boolean isAssociation() {
		return pluralAttributeElementBinding.getPluralAttributeElementNature().isAssociation();
	}

	@Override
	public TableSpecification getCollectionTable() {
		return collectionTable;
	}

	public void setCollectionTable(Table collectionTable) {
		this.collectionTable = collectionTable;
	}

	@Override
	public PluralAttributeKeyBinding getPluralAttributeKeyBinding() {
		return pluralAttributeKeyBinding;
	}

	@Override
	public AbstractPluralAttributeElementBinding getPluralAttributeElementBinding() {
		return pluralAttributeElementBinding;
	}

	@Override
	public String getCustomLoaderName() {
		return customLoaderName;
	}

	public void setCustomLoaderName(String customLoaderName) {
		this.customLoaderName = customLoaderName;
	}

	@Override
	public CustomSQL getCustomSqlInsert() {
		return customSqlInsert;
	}

	public void setCustomSqlInsert(CustomSQL customSqlInsert) {
		this.customSqlInsert = customSqlInsert;
	}

	@Override
	public CustomSQL getCustomSqlUpdate() {
		return customSqlUpdate;
	}

	public void setCustomSqlUpdate(CustomSQL customSqlUpdate) {
		this.customSqlUpdate = customSqlUpdate;
	}

	@Override
	public CustomSQL getCustomSqlDelete() {
		return customSqlDelete;
	}

	public void setCustomSqlDelete(CustomSQL customSqlDelete) {
		this.customSqlDelete = customSqlDelete;
	}

	@Override
	public CustomSQL getCustomSqlDeleteAll() {
		return customSqlDeleteAll;
	}

	public void setCustomSqlDeleteAll(CustomSQL customSqlDeleteAll) {
		this.customSqlDeleteAll = customSqlDeleteAll;
	}

	public Class<? extends CollectionPersister> getCollectionPersisterClass() {
		return collectionPersisterClass;
	}

	public void setCollectionPersisterClass(Class<? extends CollectionPersister> collectionPersisterClass) {
		this.collectionPersisterClass = collectionPersisterClass;
	}

	public Caching getCaching() {
		return caching;
	}

	public void setCaching(Caching caching) {
		this.caching = caching;
	}

	@Override
	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	@Override
	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	@Override
	public boolean isInverse() {
		return inverse;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	@Override
	public boolean isMutable() {
		return mutable;
	}

	public void setMutable(boolean mutable) {
		this.mutable = mutable;
	}

	@Override
	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}











	public String getReferencedPropertyName() {
		return referencedPropertyName;
	}

	@Override
	public boolean isSorted() {
		return sorted;
	}

	@Override
	public Comparator getComparator() {
		return comparator;
	}

	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}

	public String getComparatorClassName() {
		return comparatorClassName;
	}

	public void addFilter(String name, String condition) {
		filters.put( name, condition );
	}

	@Override
	public java.util.Map getFilterMap() {
		return filters;
	}
}