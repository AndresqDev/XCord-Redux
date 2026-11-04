package com.mysql.cj.x.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.ProtocolStringList;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxCrud {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Column_descriptor = getDescriptor().getMessageTypes().get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Column_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_Column_descriptor, new String[]{"Name", "Alias", "DocumentPath"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Projection_descriptor = getDescriptor().getMessageTypes().get(1);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Projection_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_Projection_descriptor, new String[]{"Source", "Alias"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Collection_descriptor = getDescriptor().getMessageTypes().get(2);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Collection_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_Collection_descriptor, new String[]{"Name", "Schema"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Limit_descriptor = getDescriptor().getMessageTypes().get(3);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Limit_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_Limit_descriptor, new String[]{"RowCount", "Offset"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_LimitExpr_descriptor = getDescriptor().getMessageTypes().get(4);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_LimitExpr_descriptor, new String[]{"RowCount", "Offset"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Order_descriptor = getDescriptor().getMessageTypes().get(5);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Order_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_Order_descriptor, new String[]{"Expr", "Direction"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_UpdateOperation_descriptor = getDescriptor().getMessageTypes().get(6);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_UpdateOperation_descriptor, new String[]{"Source", "Operation", "Value"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Find_descriptor = getDescriptor().getMessageTypes().get(7);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Find_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_Find_descriptor,
      new String[]{
         "Collection",
         "DataModel",
         "Projection",
         "Args",
         "Criteria",
         "Limit",
         "Order",
         "Grouping",
         "GroupingCriteria",
         "Locking",
         "LockingOptions",
         "LimitExpr"
      }
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Insert_descriptor = getDescriptor().getMessageTypes().get(8);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Insert_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_Insert_descriptor, new String[]{"Collection", "DataModel", "Projection", "Row", "Args", "Upsert"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor = internal_static_Mysqlx_Crud_Insert_descriptor.getNestedTypes()
      .get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor, new String[]{"Field"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Update_descriptor = getDescriptor().getMessageTypes().get(9);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Update_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_Update_descriptor, new String[]{"Collection", "DataModel", "Criteria", "Limit", "Order", "Operation", "Args", "LimitExpr"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Delete_descriptor = getDescriptor().getMessageTypes().get(10);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Delete_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_Delete_descriptor, new String[]{"Collection", "DataModel", "Criteria", "Limit", "Order", "Args", "LimitExpr"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_CreateView_descriptor = getDescriptor().getMessageTypes().get(11);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_CreateView_descriptor,
      new String[]{"Collection", "Definer", "Algorithm", "Security", "Check", "Column", "Stmt", "ReplaceExisting"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_ModifyView_descriptor = getDescriptor().getMessageTypes().get(12);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_ModifyView_descriptor, new String[]{"Collection", "Definer", "Algorithm", "Security", "Check", "Column", "Stmt"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_DropView_descriptor = getDescriptor().getMessageTypes().get(13);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_DropView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Crud_DropView_descriptor, new String[]{"Collection", "IfExists"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxCrud() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
   }

   public static void registerAllExtensions(ExtensionRegistry registry) {
      registerAllExtensions((ExtensionRegistryLite)registry);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      String[] descriptorData = new String[]{
         "\n\u0011mysqlx_crud.proto\u0012\u000bMysqlx.Crud\u001a\fmysqlx.proto\u001a\u0011mysqlx_expr.proto\u001a\u0016mysqlx_datatypes.proto\"[\n\u0006Column\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\r\n\u0005alias\u0018\u0002 \u0001(\t\u00124\n\rdocument_path\u0018\u0003 \u0003(\u000b2\u001d.Mysqlx.Expr.DocumentPathItem\">\n\nProjection\u0012!\n\u0006source\u0018\u0001 \u0002(\u000b2\u0011.Mysqlx.Expr.Expr\u0012\r\n\u0005alias\u0018\u0002 \u0001(\t\"*\n\nCollection\u0012\f\n\u0004name\u0018\u0001 \u0002(\t\u0012\u000e\n\u0006schema\u0018\u0002 \u0001(\t\"*\n\u0005Limit\u0012\u0011\n\trow_count\u0018\u0001 \u0002(\u0004\u0012\u000e\n\u0006offset\u0018\u0002 \u0001(\u0004\"T\n\tLimitExpr\u0012$\n\trow_count\u0018\u0001 \u0002(\u000b2\u0011.Mysqlx.Expr.Expr\u0012!\n\u0006offset\u0018\u0002 \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\"~\n\u0005Order\u0012\u001f\n\u0004expr\u0018\u0001 \u0002(\u000b2\u0011.Mysqlx.Expr.Expr\u00124\n\tdirection\u0018\u0002 \u0001(\u000e2\u001c.Mysqlx.Crud.Order.Direction:\u0003ASC\"\u001e\n\tDirection\u0012\u0007\n\u0003ASC\u0010\u0001\u0012\b\n\u0004DESC\u0010\u0002\"¬\u0002\n\u000fUpdateOperation\u0012-\n\u0006source\u0018\u0001 \u0002(\u000b2\u001d.Mysqlx.Expr.ColumnIdentifier\u0012:\n\toperation\u0018\u0002 \u0002(\u000e2'.Mysqlx.Crud.UpdateOperation.UpdateType\u0012 \n\u0005value\u0018\u0003 \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\"\u008b\u0001\n\nUpdateType\u0012\u0007\n\u0003SET\u0010\u0001\u0012\u000f\n\u000bITEM_REMOVE\u0010\u0002\u0012\f\n\bITEM_SET\u0010\u0003\u0012\u0010\n\fITEM_REPLACE\u0010\u0004\u0012\u000e\n\nITEM_MERGE\u0010\u0005\u0012\u0010\n\fARRAY_INSERT\u0010\u0006\u0012\u0010\n\fARRAY_APPEND\u0010\u0007\u0012\u000f\n\u000bMERGE_PATCH\u0010\b\"ê\u0004\n\u0004Find\u0012+\n\ncollection\u0018\u0002 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012*\n\ndata_model\u0018\u0003 \u0001(\u000e2\u0016.Mysqlx.Crud.DataModel\u0012+\n\nprojection\u0018\u0004 \u0003(\u000b2\u0017.Mysqlx.Crud.Projection\u0012&\n\u0004args\u0018\u000b \u0003(\u000b2\u0018.Mysqlx.Datatypes.Scalar\u0012#\n\bcriteria\u0018\u0005 \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\u0012!\n\u0005limit\u0018\u0006 \u0001(\u000b2\u0012.Mysqlx.Crud.Limit\u0012!\n\u0005order\u0018\u0007 \u0003(\u000b2\u0012.Mysqlx.Crud.Order\u0012#\n\bgrouping\u0018\b \u0003(\u000b2\u0011.Mysqlx.Expr.Expr\u0012,\n\u0011grouping_criteria\u0018\t \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\u0012*\n\u0007locking\u0018\f \u0001(\u000e2\u0019.Mysqlx.Crud.Find.RowLock\u00129\n\u000flocking_options\u0018\r \u0001(\u000e2 .Mysqlx.Crud.Find.RowLockOptions\u0012*\n\nlimit_expr\u0018\u000e \u0001(\u000b2\u0016.Mysqlx.Crud.LimitExpr\".\n\u0007RowLock\u0012\u000f\n\u000bSHARED_LOCK\u0010\u0001\u0012\u0012\n\u000eEXCLUSIVE_LOCK\u0010\u0002\"-\n\u000eRowLockOptions\u0012\n\n\u0006NOWAIT\u0010\u0001\u0012\u000f\n\u000bSKIP_LOCKED\u0010\u0002:\u0004\u0088ê0\u0011\"¨\u0002\n\u0006Insert\u0012+\n\ncollection\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012*\n\ndata_model\u0018\u0002 \u0001(\u000e2\u0016.Mysqlx.Crud.DataModel\u0012'\n\nprojection\u0018\u0003 \u0003(\u000b2\u0013.Mysqlx.Crud.Column\u0012)\n\u0003row\u0018\u0004 \u0003(\u000b2\u001c.Mysqlx.Crud.Insert.TypedRow\u0012&\n\u0004args\u0018\u0005 \u0003(\u000b2\u0018.Mysqlx.Datatypes.Scalar\u0012\u0015\n\u0006upsert\u0018\u0006 \u0001(\b:\u0005false\u001a,\n\bTypedRow\u0012 \n\u0005field\u0018\u0001 \u0003(\u000b2\u0011.Mysqlx.Expr.Expr:\u0004\u0088ê0\u0012\"×\u0002\n\u0006Update\u0012+\n\ncollection\u0018\u0002 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012*\n\ndata_model\u0018\u0003 \u0001(\u000e2\u0016.Mysqlx.Crud.DataModel\u0012#\n\bcriteria\u0018\u0004 \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\u0012!\n\u0005limit\u0018\u0005 \u0001(\u000b2\u0012.Mysqlx.Crud.Limit\u0012!\n\u0005order\u0018\u0006 \u0003(\u000b2\u0012.Mysqlx.Crud.Order\u0012/\n\toperation\u0018\u0007 \u0003(\u000b2\u001c.Mysqlx.Crud.UpdateOperation\u0012&\n\u0004args\u0018\b \u0003(\u000b2\u0018.Mysqlx.Datatypes.Scalar\u0012*\n\nlimit_expr\u0018\t \u0001(\u000b2\u0016.Mysqlx.Crud.LimitExpr:\u0004\u0088ê0\u0013\"¦\u0002\n\u0006Delete\u0012+\n\ncollection\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012*\n\ndata_model\u0018\u0002 \u0001(\u000e2\u0016.Mysqlx.Crud.DataModel\u0012#\n\bcriteria\u0018\u0003 \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\u0012!\n\u0005limit\u0018\u0004 \u0001(\u000b2\u0012.Mysqlx.Crud.Limit\u0012!\n\u0005order\u0018\u0005 \u0003(\u000b2\u0012.Mysqlx.Crud.Order\u0012&\n\u0004args\u0018\u0006 \u0003(\u000b2\u0018.Mysqlx.Datatypes.Scalar\u0012*\n\nlimit_expr\u0018\u0007 \u0001(\u000b2\u0016.Mysqlx.Crud.LimitExpr:\u0004\u0088ê0\u0014\"Â\u0002\n\nCreateView\u0012+\n\ncollection\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012\u000f\n\u0007definer\u0018\u0002 \u0001(\t\u00128\n\talgorithm\u0018\u0003 \u0001(\u000e2\u001a.Mysqlx.Crud.ViewAlgorithm:\tUNDEFINED\u00127\n\bsecurity\u0018\u0004 \u0001(\u000e2\u001c.Mysqlx.Crud.ViewSqlSecurity:\u0007DEFINER\u0012+\n\u0005check\u0018\u0005 \u0001(\u000e2\u001c.Mysqlx.Crud.ViewCheckOption\u0012\u000e\n\u0006column\u0018\u0006 \u0003(\t\u0012\u001f\n\u0004stmt\u0018\u0007 \u0002(\u000b2\u0011.Mysqlx.Crud.Find\u0012\u001f\n\u0010replace_existing\u0018\b \u0001(\b:\u0005false:\u0004\u0088ê0\u001e\"\u008d\u0002\n\nModifyView\u0012+\n\ncollection\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012\u000f\n\u0007definer\u0018\u0002 \u0001(\t\u0012-\n\talgorithm\u0018\u0003 \u0001(\u000e2\u001a.Mysqlx.Crud.ViewAlgorithm\u0012.\n\bsecurity\u0018\u0004 \u0001(\u000e2\u001c.Mysqlx.Crud.ViewSqlSecurity\u0012+\n\u0005check\u0018\u0005 \u0001(\u000e2\u001c.Mysqlx.Crud.ViewCheckOption\u0012\u000e\n\u0006column\u0018\u0006 \u0003(\t\u0012\u001f\n\u0004stmt\u0018\u0007 \u0001(\u000b2\u0011.Mysqlx.Crud.Find:\u0004\u0088ê0\u001f\"W\n\bDropView\u0012+\n\ncollection\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012\u0018\n\tif_exists\u0018\u0002 \u0001(\b:\u0005false:\u0004\u0088ê0 *$\n\tDataModel\u0012\f\n\bDOCUMENT\u0010\u0001\u0012\t\n\u0005TABLE\u0010\u0002*8\n\rViewAlgorithm\u0012\r\n\tUNDEFINED\u0010\u0001\u0012\t\n\u0005MERGE\u0010\u0002\u0012\r\n\tTEMPTABLE\u0010\u0003*+\n\u000fViewSqlSecurity\u0012\u000b\n\u0007INVOKER\u0010\u0001\u0012\u000b\n\u0007DEFINER\u0010\u0002**\n\u000fViewCheckOption\u0012\t\n\u0005LOCAL\u0010\u0001\u0012\f\n\bCASCADED\u0010\u0002B\u0019\n\u0017com.mysql.cj.x.protobuf"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
         descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor(), MysqlxExpr.getDescriptor(), MysqlxDatatypes.getDescriptor()}
      );
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
      MysqlxExpr.getDescriptor();
      MysqlxDatatypes.getDescriptor();
   }

   public static final class Collection extends GeneratedMessageV3 implements MysqlxCrud.CollectionOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private volatile Object name_;
      public static final int SCHEMA_FIELD_NUMBER = 2;
      private volatile Object schema_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.Collection DEFAULT_INSTANCE = new MysqlxCrud.Collection();
      @Deprecated
      public static final Parser<MysqlxCrud.Collection> PARSER = new AbstractParser<MysqlxCrud.Collection>() {
         public MysqlxCrud.Collection parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.Collection(input, extensionRegistry);
         }
      };

      private Collection(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Collection() {
         this.name_ = "";
         this.schema_ = "";
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.Collection();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Collection(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10: {
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.name_ = bs;
                        break;
                     }
                     case 18: {
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.schema_ = bs;
                        break;
                     }
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.Collection.class, MysqlxCrud.Collection.Builder.class);
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         Object ref = this.name_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.name_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getNameBytes() {
         Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasSchema() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getSchema() {
         Object ref = this.schema_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.schema_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getSchemaBytes() {
         Object ref = this.schema_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.schema_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasName()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 1, this.name_);
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.schema_);
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(1, this.name_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.schema_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.Collection)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.Collection other = (MysqlxCrud.Collection)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (this.hasSchema() != other.hasSchema()) {
               return false;
            } else {
               return this.hasSchema() && !this.getSchema().equals(other.getSchema()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.hasSchema()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getSchema().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.Collection parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Collection parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Collection parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Collection parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Collection parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Collection parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Collection parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Collection parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Collection parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Collection parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Collection parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Collection parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.Collection.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.Collection.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.Collection.Builder newBuilder(MysqlxCrud.Collection prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.Collection.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.Collection.Builder() : new MysqlxCrud.Collection.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.Collection.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.Collection.Builder(parent);
      }

      public static MysqlxCrud.Collection getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.Collection> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.Collection> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.Collection getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.Collection.Builder> implements MysqlxCrud.CollectionOrBuilder {
         private int bitField0_;
         private Object name_ = "";
         private Object schema_ = "";

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.Collection.class, MysqlxCrud.Collection.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Collection.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxCrud.Collection.Builder clear() {
            super.clear();
            this.name_ = "";
            this.bitField0_ &= -2;
            this.schema_ = "";
            this.bitField0_ &= -3;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor;
         }

         public MysqlxCrud.Collection getDefaultInstanceForType() {
            return MysqlxCrud.Collection.getDefaultInstance();
         }

         public MysqlxCrud.Collection build() {
            MysqlxCrud.Collection result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.Collection buildPartial() {
            MysqlxCrud.Collection result = new MysqlxCrud.Collection(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.schema_ = this.schema_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.Collection.Builder clone() {
            return (MysqlxCrud.Collection.Builder)super.clone();
         }

         public MysqlxCrud.Collection.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Collection.Builder)super.setField(field, value);
         }

         public MysqlxCrud.Collection.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.Collection.Builder)super.clearField(field);
         }

         public MysqlxCrud.Collection.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.Collection.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.Collection.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.Collection.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.Collection.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Collection.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.Collection.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.Collection) {
               return this.mergeFrom((MysqlxCrud.Collection)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.Collection.Builder mergeFrom(MysqlxCrud.Collection other) {
            if (other == MysqlxCrud.Collection.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.bitField0_ |= 1;
                  this.name_ = other.name_;
                  this.onChanged();
               }

               if (other.hasSchema()) {
                  this.bitField0_ |= 2;
                  this.schema_ = other.schema_;
                  this.onChanged();
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return this.hasName();
         }

         public MysqlxCrud.Collection.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.Collection parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.Collection.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.Collection)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getName() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getNameBytes() {
            Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxCrud.Collection.Builder setName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Collection.Builder clearName() {
            this.bitField0_ &= -2;
            this.name_ = MysqlxCrud.Collection.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public MysqlxCrud.Collection.Builder setNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasSchema() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getSchema() {
            Object ref = this.schema_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.schema_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getSchemaBytes() {
            Object ref = this.schema_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.schema_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxCrud.Collection.Builder setSchema(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.schema_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Collection.Builder clearSchema() {
            this.bitField0_ &= -3;
            this.schema_ = MysqlxCrud.Collection.getDefaultInstance().getSchema();
            this.onChanged();
            return this;
         }

         public MysqlxCrud.Collection.Builder setSchemaBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.schema_ = value;
               this.onChanged();
               return this;
            }
         }

         public final MysqlxCrud.Collection.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Collection.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.Collection.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Collection.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface CollectionOrBuilder extends MessageOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasSchema();

      String getSchema();

      ByteString getSchemaBytes();
   }

   public static final class Column extends GeneratedMessageV3 implements MysqlxCrud.ColumnOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private volatile Object name_;
      public static final int ALIAS_FIELD_NUMBER = 2;
      private volatile Object alias_;
      public static final int DOCUMENT_PATH_FIELD_NUMBER = 3;
      private List<MysqlxExpr.DocumentPathItem> documentPath_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.Column DEFAULT_INSTANCE = new MysqlxCrud.Column();
      @Deprecated
      public static final Parser<MysqlxCrud.Column> PARSER = new AbstractParser<MysqlxCrud.Column>() {
         public MysqlxCrud.Column parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.Column(input, extensionRegistry);
         }
      };

      private Column(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Column() {
         this.name_ = "";
         this.alias_ = "";
         this.documentPath_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.Column();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Column(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10: {
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.name_ = bs;
                        break;
                     }
                     case 18: {
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.alias_ = bs;
                        break;
                     }
                     case 26:
                        if ((mutable_bitField0_ & 4) == 0) {
                           this.documentPath_ = new ArrayList<>();
                           mutable_bitField0_ |= 4;
                        }

                        this.documentPath_.add(input.readMessage(MysqlxExpr.DocumentPathItem.PARSER, extensionRegistry));
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 4) != 0) {
                  this.documentPath_ = Collections.unmodifiableList(this.documentPath_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Column_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.Column.class, MysqlxCrud.Column.Builder.class);
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         Object ref = this.name_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.name_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getNameBytes() {
         Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasAlias() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getAlias() {
         Object ref = this.alias_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.alias_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getAliasBytes() {
         Object ref = this.alias_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.alias_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() {
         return this.documentPath_;
      }

      @Override
      public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() {
         return this.documentPath_;
      }

      @Override
      public int getDocumentPathCount() {
         return this.documentPath_.size();
      }

      @Override
      public MysqlxExpr.DocumentPathItem getDocumentPath(int index) {
         return this.documentPath_.get(index);
      }

      @Override
      public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) {
         return this.documentPath_.get(index);
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for (int i = 0; i < this.getDocumentPathCount(); i++) {
               if (!this.getDocumentPath(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 1, this.name_);
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.alias_);
         }

         for (int i = 0; i < this.documentPath_.size(); i++) {
            output.writeMessage(3, this.documentPath_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(1, this.name_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.alias_);
            }

            for (int i = 0; i < this.documentPath_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(3, this.documentPath_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.Column)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.Column other = (MysqlxCrud.Column)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (this.hasAlias() != other.hasAlias()) {
               return false;
            } else if (this.hasAlias() && !this.getAlias().equals(other.getAlias())) {
               return false;
            } else {
               return !this.getDocumentPathList().equals(other.getDocumentPathList()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.hasAlias()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getAlias().hashCode();
            }

            if (this.getDocumentPathCount() > 0) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getDocumentPathList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.Column parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Column parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Column parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Column parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Column parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Column parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Column parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Column parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Column parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Column parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Column parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Column parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.Column.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.Column.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.Column.Builder newBuilder(MysqlxCrud.Column prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.Column.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.Column.Builder() : new MysqlxCrud.Column.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.Column.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.Column.Builder(parent);
      }

      public static MysqlxCrud.Column getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.Column> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.Column> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.Column getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.Column.Builder> implements MysqlxCrud.ColumnOrBuilder {
         private int bitField0_;
         private Object name_ = "";
         private Object alias_ = "";
         private List<MysqlxExpr.DocumentPathItem> documentPath_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> documentPathBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Column_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.Column.class, MysqlxCrud.Column.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Column.alwaysUseFieldBuilders) {
               this.getDocumentPathFieldBuilder();
            }
         }

         public MysqlxCrud.Column.Builder clear() {
            super.clear();
            this.name_ = "";
            this.bitField0_ &= -2;
            this.alias_ = "";
            this.bitField0_ &= -3;
            if (this.documentPathBuilder_ == null) {
               this.documentPath_ = Collections.emptyList();
               this.bitField0_ &= -5;
            } else {
               this.documentPathBuilder_.clear();
            }

            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor;
         }

         public MysqlxCrud.Column getDefaultInstanceForType() {
            return MysqlxCrud.Column.getDefaultInstance();
         }

         public MysqlxCrud.Column build() {
            MysqlxCrud.Column result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.Column buildPartial() {
            MysqlxCrud.Column result = new MysqlxCrud.Column(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.alias_ = this.alias_;
            if (this.documentPathBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0) {
                  this.documentPath_ = Collections.unmodifiableList(this.documentPath_);
                  this.bitField0_ &= -5;
               }

               result.documentPath_ = this.documentPath_;
            } else {
               result.documentPath_ = this.documentPathBuilder_.build();
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.Column.Builder clone() {
            return (MysqlxCrud.Column.Builder)super.clone();
         }

         public MysqlxCrud.Column.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Column.Builder)super.setField(field, value);
         }

         public MysqlxCrud.Column.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.Column.Builder)super.clearField(field);
         }

         public MysqlxCrud.Column.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.Column.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.Column.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.Column.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.Column.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Column.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.Column.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.Column) {
               return this.mergeFrom((MysqlxCrud.Column)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.Column.Builder mergeFrom(MysqlxCrud.Column other) {
            if (other == MysqlxCrud.Column.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.bitField0_ |= 1;
                  this.name_ = other.name_;
                  this.onChanged();
               }

               if (other.hasAlias()) {
                  this.bitField0_ |= 2;
                  this.alias_ = other.alias_;
                  this.onChanged();
               }

               if (this.documentPathBuilder_ == null) {
                  if (!other.documentPath_.isEmpty()) {
                     if (this.documentPath_.isEmpty()) {
                        this.documentPath_ = other.documentPath_;
                        this.bitField0_ &= -5;
                     } else {
                        this.ensureDocumentPathIsMutable();
                        this.documentPath_.addAll(other.documentPath_);
                     }

                     this.onChanged();
                  }
               } else if (!other.documentPath_.isEmpty()) {
                  if (this.documentPathBuilder_.isEmpty()) {
                     this.documentPathBuilder_.dispose();
                     this.documentPathBuilder_ = null;
                     this.documentPath_ = other.documentPath_;
                     this.bitField0_ &= -5;
                     this.documentPathBuilder_ = MysqlxCrud.Column.alwaysUseFieldBuilders ? this.getDocumentPathFieldBuilder() : null;
                  } else {
                     this.documentPathBuilder_.addAllMessages(other.documentPath_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            for (int i = 0; i < this.getDocumentPathCount(); i++) {
               if (!this.getDocumentPath(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public MysqlxCrud.Column.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.Column parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.Column.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.Column)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getName() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getNameBytes() {
            Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxCrud.Column.Builder setName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Column.Builder clearName() {
            this.bitField0_ &= -2;
            this.name_ = MysqlxCrud.Column.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public MysqlxCrud.Column.Builder setNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasAlias() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getAlias() {
            Object ref = this.alias_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.alias_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getAliasBytes() {
            Object ref = this.alias_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.alias_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxCrud.Column.Builder setAlias(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.alias_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Column.Builder clearAlias() {
            this.bitField0_ &= -3;
            this.alias_ = MysqlxCrud.Column.getDefaultInstance().getAlias();
            this.onChanged();
            return this;
         }

         public MysqlxCrud.Column.Builder setAliasBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.alias_ = value;
               this.onChanged();
               return this;
            }
         }

         private void ensureDocumentPathIsMutable() {
            if ((this.bitField0_ & 4) == 0) {
               this.documentPath_ = new ArrayList<>(this.documentPath_);
               this.bitField0_ |= 4;
            }
         }

         @Override
         public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() {
            return this.documentPathBuilder_ == null ? Collections.unmodifiableList(this.documentPath_) : this.documentPathBuilder_.getMessageList();
         }

         @Override
         public int getDocumentPathCount() {
            return this.documentPathBuilder_ == null ? this.documentPath_.size() : this.documentPathBuilder_.getCount();
         }

         @Override
         public MysqlxExpr.DocumentPathItem getDocumentPath(int index) {
            return this.documentPathBuilder_ == null ? this.documentPath_.get(index) : this.documentPathBuilder_.getMessage(index);
         }

         public MysqlxCrud.Column.Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.set(index, value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Column.Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Column.Builder addDocumentPath(MysqlxExpr.DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Column.Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(index, value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Column.Builder addDocumentPath(MysqlxExpr.DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Column.Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Column.Builder addAllDocumentPath(Iterable<? extends MysqlxExpr.DocumentPathItem> values) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.documentPath_);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Column.Builder clearDocumentPath() {
            if (this.documentPathBuilder_ == null) {
               this.documentPath_ = Collections.emptyList();
               this.bitField0_ &= -5;
               this.onChanged();
            } else {
               this.documentPathBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Column.Builder removeDocumentPath(int index) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.remove(index);
               this.onChanged();
            } else {
               this.documentPathBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxExpr.DocumentPathItem.Builder getDocumentPathBuilder(int index) {
            return this.getDocumentPathFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) {
            return this.documentPathBuilder_ == null ? this.documentPath_.get(index) : this.documentPathBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() {
            return this.documentPathBuilder_ != null ? this.documentPathBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.documentPath_);
         }

         public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder() {
            return this.getDocumentPathFieldBuilder().addBuilder(MysqlxExpr.DocumentPathItem.getDefaultInstance());
         }

         public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder(int index) {
            return this.getDocumentPathFieldBuilder().addBuilder(index, MysqlxExpr.DocumentPathItem.getDefaultInstance());
         }

         public List<MysqlxExpr.DocumentPathItem.Builder> getDocumentPathBuilderList() {
            return this.getDocumentPathFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathFieldBuilder() {
            if (this.documentPathBuilder_ == null) {
               this.documentPathBuilder_ = new RepeatedFieldBuilderV3<>(
                  this.documentPath_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean()
               );
               this.documentPath_ = null;
            }

            return this.documentPathBuilder_;
         }

         public final MysqlxCrud.Column.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Column.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.Column.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Column.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface ColumnOrBuilder extends MessageOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasAlias();

      String getAlias();

      ByteString getAliasBytes();

      List<MysqlxExpr.DocumentPathItem> getDocumentPathList();

      MysqlxExpr.DocumentPathItem getDocumentPath(int var1);

      int getDocumentPathCount();

      List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList();

      MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int var1);
   }

   public static final class CreateView extends GeneratedMessageV3 implements MysqlxCrud.CreateViewOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 1;
      private MysqlxCrud.Collection collection_;
      public static final int DEFINER_FIELD_NUMBER = 2;
      private volatile Object definer_;
      public static final int ALGORITHM_FIELD_NUMBER = 3;
      private int algorithm_;
      public static final int SECURITY_FIELD_NUMBER = 4;
      private int security_;
      public static final int CHECK_FIELD_NUMBER = 5;
      private int check_;
      public static final int COLUMN_FIELD_NUMBER = 6;
      private LazyStringList column_;
      public static final int STMT_FIELD_NUMBER = 7;
      private MysqlxCrud.Find stmt_;
      public static final int REPLACE_EXISTING_FIELD_NUMBER = 8;
      private boolean replaceExisting_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.CreateView DEFAULT_INSTANCE = new MysqlxCrud.CreateView();
      @Deprecated
      public static final Parser<MysqlxCrud.CreateView> PARSER = new AbstractParser<MysqlxCrud.CreateView>() {
         public MysqlxCrud.CreateView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.CreateView(input, extensionRegistry);
         }
      };

      private CreateView(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private CreateView() {
         this.definer_ = "";
         this.algorithm_ = 1;
         this.security_ = 2;
         this.check_ = 1;
         this.column_ = LazyStringArrayList.EMPTY;
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.CreateView();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private CreateView(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxCrud.Collection.Builder subBuilderx = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilderx = this.collection_.toBuilder();
                        }

                        this.collection_ = input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilderx != null) {
                           subBuilderx.mergeFrom(this.collection_);
                           this.collection_ = subBuilderx.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 18: {
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.definer_ = bs;
                        break;
                     }
                     case 24:
                        int rawValuexx = input.readEnum();
                        MysqlxCrud.ViewAlgorithm valuexx = MysqlxCrud.ViewAlgorithm.valueOf(rawValuexx);
                        if (valuexx == null) {
                           unknownFields.mergeVarintField(3, rawValuexx);
                        } else {
                           this.bitField0_ |= 4;
                           this.algorithm_ = rawValuexx;
                        }
                        break;
                     case 32:
                        int rawValuex = input.readEnum();
                        MysqlxCrud.ViewSqlSecurity valuex = MysqlxCrud.ViewSqlSecurity.valueOf(rawValuex);
                        if (valuex == null) {
                           unknownFields.mergeVarintField(4, rawValuex);
                        } else {
                           this.bitField0_ |= 8;
                           this.security_ = rawValuex;
                        }
                        break;
                     case 40:
                        int rawValue = input.readEnum();
                        MysqlxCrud.ViewCheckOption value = MysqlxCrud.ViewCheckOption.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(5, rawValue);
                        } else {
                           this.bitField0_ |= 16;
                           this.check_ = rawValue;
                        }
                        break;
                     case 50: {
                        ByteString bs = input.readBytes();
                        if ((mutable_bitField0_ & 32) == 0) {
                           this.column_ = new LazyStringArrayList();
                           mutable_bitField0_ |= 32;
                        }

                        this.column_.add(bs);
                        break;
                     }
                     case 58:
                        MysqlxCrud.Find.Builder subBuilder = null;
                        if ((this.bitField0_ & 32) != 0) {
                           subBuilder = this.stmt_.toBuilder();
                        }

                        this.stmt_ = input.readMessage(MysqlxCrud.Find.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.stmt_);
                           this.stmt_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 32;
                        break;
                     case 64:
                        this.bitField0_ |= 64;
                        this.replaceExisting_ = input.readBool();
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw new InvalidProtocolBufferException(var14).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 32) != 0) {
                  this.column_ = this.column_.getUnmodifiableView();
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.CreateView.class, MysqlxCrud.CreateView.Builder.class);
      }

      @Override
      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxCrud.Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public boolean hasDefiner() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getDefiner() {
         Object ref = this.definer_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.definer_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getDefinerBytes() {
         Object ref = this.definer_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.definer_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasAlgorithm() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public MysqlxCrud.ViewAlgorithm getAlgorithm() {
         MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
         return result == null ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
      }

      @Override
      public boolean hasSecurity() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public MysqlxCrud.ViewSqlSecurity getSecurity() {
         MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
         return result == null ? MysqlxCrud.ViewSqlSecurity.DEFINER : result;
      }

      @Override
      public boolean hasCheck() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public MysqlxCrud.ViewCheckOption getCheck() {
         MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
         return result == null ? MysqlxCrud.ViewCheckOption.LOCAL : result;
      }

      public ProtocolStringList getColumnList() {
         return this.column_;
      }

      @Override
      public int getColumnCount() {
         return this.column_.size();
      }

      @Override
      public String getColumn(int index) {
         return this.column_.get(index);
      }

      @Override
      public ByteString getColumnBytes(int index) {
         return this.column_.getByteString(index);
      }

      @Override
      public boolean hasStmt() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public MysqlxCrud.Find getStmt() {
         return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
      }

      @Override
      public MysqlxCrud.FindOrBuilder getStmtOrBuilder() {
         return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
      }

      @Override
      public boolean hasReplaceExisting() {
         return (this.bitField0_ & 64) != 0;
      }

      @Override
      public boolean getReplaceExisting() {
         return this.replaceExisting_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.hasStmt()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getStmt().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.definer_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeEnum(3, this.algorithm_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeEnum(4, this.security_);
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeEnum(5, this.check_);
         }

         for (int i = 0; i < this.column_.size(); i++) {
            GeneratedMessageV3.writeString(output, 6, this.column_.getRaw(i));
         }

         if ((this.bitField0_ & 32) != 0) {
            output.writeMessage(7, this.getStmt());
         }

         if ((this.bitField0_ & 64) != 0) {
            output.writeBool(8, this.replaceExisting_);
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.definer_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeEnumSize(3, this.algorithm_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeEnumSize(4, this.security_);
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeEnumSize(5, this.check_);
            }

            int dataSize = 0;

            for (int i = 0; i < this.column_.size(); i++) {
               dataSize += computeStringSizeNoTag(this.column_.getRaw(i));
            }

            size += dataSize;
            size += 1 * this.getColumnList().size();
            if ((this.bitField0_ & 32) != 0) {
               size += CodedOutputStream.computeMessageSize(7, this.getStmt());
            }

            if ((this.bitField0_ & 64) != 0) {
               size += CodedOutputStream.computeBoolSize(8, this.replaceExisting_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.CreateView)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.CreateView other = (MysqlxCrud.CreateView)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDefiner() != other.hasDefiner()) {
               return false;
            } else if (this.hasDefiner() && !this.getDefiner().equals(other.getDefiner())) {
               return false;
            } else if (this.hasAlgorithm() != other.hasAlgorithm()) {
               return false;
            } else if (this.hasAlgorithm() && this.algorithm_ != other.algorithm_) {
               return false;
            } else if (this.hasSecurity() != other.hasSecurity()) {
               return false;
            } else if (this.hasSecurity() && this.security_ != other.security_) {
               return false;
            } else if (this.hasCheck() != other.hasCheck()) {
               return false;
            } else if (this.hasCheck() && this.check_ != other.check_) {
               return false;
            } else if (!this.getColumnList().equals(other.getColumnList())) {
               return false;
            } else if (this.hasStmt() != other.hasStmt()) {
               return false;
            } else if (this.hasStmt() && !this.getStmt().equals(other.getStmt())) {
               return false;
            } else if (this.hasReplaceExisting() != other.hasReplaceExisting()) {
               return false;
            } else {
               return this.hasReplaceExisting() && this.getReplaceExisting() != other.getReplaceExisting()
                  ? false
                  : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDefiner()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getDefiner().hashCode();
            }

            if (this.hasAlgorithm()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.algorithm_;
            }

            if (this.hasSecurity()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.security_;
            }

            if (this.hasCheck()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.check_;
            }

            if (this.getColumnCount() > 0) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getColumnList().hashCode();
            }

            if (this.hasStmt()) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getStmt().hashCode();
            }

            if (this.hasReplaceExisting()) {
               hash = 37 * hash + 8;
               hash = 53 * hash + Internal.hashBoolean(this.getReplaceExisting());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.CreateView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.CreateView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.CreateView parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.CreateView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.CreateView parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.CreateView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.CreateView parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.CreateView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.CreateView parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.CreateView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.CreateView parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.CreateView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.CreateView.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.CreateView.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.CreateView.Builder newBuilder(MysqlxCrud.CreateView prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.CreateView.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.CreateView.Builder() : new MysqlxCrud.CreateView.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.CreateView.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.CreateView.Builder(parent);
      }

      public static MysqlxCrud.CreateView getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.CreateView> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.CreateView> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.CreateView getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.CreateView.Builder> implements MysqlxCrud.CreateViewOrBuilder {
         private int bitField0_;
         private MysqlxCrud.Collection collection_;
         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
         private Object definer_ = "";
         private int algorithm_ = 1;
         private int security_ = 2;
         private int check_ = 1;
         private LazyStringList column_ = LazyStringArrayList.EMPTY;
         private MysqlxCrud.Find stmt_;
         private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> stmtBuilder_;
         private boolean replaceExisting_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.CreateView.class, MysqlxCrud.CreateView.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.CreateView.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getStmtFieldBuilder();
            }
         }

         public MysqlxCrud.CreateView.Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.definer_ = "";
            this.bitField0_ &= -3;
            this.algorithm_ = 1;
            this.bitField0_ &= -5;
            this.security_ = 2;
            this.bitField0_ &= -9;
            this.check_ = 1;
            this.bitField0_ &= -17;
            this.column_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= -33;
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -65;
            this.replaceExisting_ = false;
            this.bitField0_ &= -129;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor;
         }

         public MysqlxCrud.CreateView getDefaultInstanceForType() {
            return MysqlxCrud.CreateView.getDefaultInstance();
         }

         public MysqlxCrud.CreateView build() {
            MysqlxCrud.CreateView result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.CreateView buildPartial() {
            MysqlxCrud.CreateView result = new MysqlxCrud.CreateView(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.definer_ = this.definer_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.algorithm_ = this.algorithm_;
            if ((from_bitField0_ & 8) != 0) {
               to_bitField0_ |= 8;
            }

            result.security_ = this.security_;
            if ((from_bitField0_ & 16) != 0) {
               to_bitField0_ |= 16;
            }

            result.check_ = this.check_;
            if ((this.bitField0_ & 32) != 0) {
               this.column_ = this.column_.getUnmodifiableView();
               this.bitField0_ &= -33;
            }

            result.column_ = this.column_;
            if ((from_bitField0_ & 64) != 0) {
               if (this.stmtBuilder_ == null) {
                  result.stmt_ = this.stmt_;
               } else {
                  result.stmt_ = this.stmtBuilder_.build();
               }

               to_bitField0_ |= 32;
            }

            if ((from_bitField0_ & 128) != 0) {
               result.replaceExisting_ = this.replaceExisting_;
               to_bitField0_ |= 64;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.CreateView.Builder clone() {
            return (MysqlxCrud.CreateView.Builder)super.clone();
         }

         public MysqlxCrud.CreateView.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.CreateView.Builder)super.setField(field, value);
         }

         public MysqlxCrud.CreateView.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.CreateView.Builder)super.clearField(field);
         }

         public MysqlxCrud.CreateView.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.CreateView.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.CreateView.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.CreateView.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.CreateView.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.CreateView.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.CreateView.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.CreateView) {
               return this.mergeFrom((MysqlxCrud.CreateView)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.CreateView.Builder mergeFrom(MysqlxCrud.CreateView other) {
            if (other == MysqlxCrud.CreateView.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDefiner()) {
                  this.bitField0_ |= 2;
                  this.definer_ = other.definer_;
                  this.onChanged();
               }

               if (other.hasAlgorithm()) {
                  this.setAlgorithm(other.getAlgorithm());
               }

               if (other.hasSecurity()) {
                  this.setSecurity(other.getSecurity());
               }

               if (other.hasCheck()) {
                  this.setCheck(other.getCheck());
               }

               if (!other.column_.isEmpty()) {
                  if (this.column_.isEmpty()) {
                     this.column_ = other.column_;
                     this.bitField0_ &= -33;
                  } else {
                     this.ensureColumnIsMutable();
                     this.column_.addAll(other.column_);
                  }

                  this.onChanged();
               }

               if (other.hasStmt()) {
                  this.mergeStmt(other.getStmt());
               }

               if (other.hasReplaceExisting()) {
                  this.setReplaceExisting(other.getReplaceExisting());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else if (!this.hasStmt()) {
               return false;
            } else {
               return !this.getCollection().isInitialized() ? false : this.getStmt().isInitialized();
            }
         }

         public MysqlxCrud.CreateView.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.CreateView parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.CreateView.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.CreateView)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxCrud.Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return this.collectionBuilder_.getMessage();
            }
         }

         public MysqlxCrud.CreateView.Builder setCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.CreateView.Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.CreateView.Builder mergeCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.CreateView.Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxCrud.Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getCollectionFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3<>(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         @Override
         public boolean hasDefiner() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getDefiner() {
            Object ref = this.definer_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.definer_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getDefinerBytes() {
            Object ref = this.definer_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.definer_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxCrud.CreateView.Builder setDefiner(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.definer_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.CreateView.Builder clearDefiner() {
            this.bitField0_ &= -3;
            this.definer_ = MysqlxCrud.CreateView.getDefaultInstance().getDefiner();
            this.onChanged();
            return this;
         }

         public MysqlxCrud.CreateView.Builder setDefinerBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.definer_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasAlgorithm() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public MysqlxCrud.ViewAlgorithm getAlgorithm() {
            MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
            return result == null ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
         }

         public MysqlxCrud.CreateView.Builder setAlgorithm(MysqlxCrud.ViewAlgorithm value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.algorithm_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.CreateView.Builder clearAlgorithm() {
            this.bitField0_ &= -5;
            this.algorithm_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasSecurity() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public MysqlxCrud.ViewSqlSecurity getSecurity() {
            MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
            return result == null ? MysqlxCrud.ViewSqlSecurity.DEFINER : result;
         }

         public MysqlxCrud.CreateView.Builder setSecurity(MysqlxCrud.ViewSqlSecurity value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.security_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.CreateView.Builder clearSecurity() {
            this.bitField0_ &= -9;
            this.security_ = 2;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasCheck() {
            return (this.bitField0_ & 16) != 0;
         }

         @Override
         public MysqlxCrud.ViewCheckOption getCheck() {
            MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
            return result == null ? MysqlxCrud.ViewCheckOption.LOCAL : result;
         }

         public MysqlxCrud.CreateView.Builder setCheck(MysqlxCrud.ViewCheckOption value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 16;
               this.check_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.CreateView.Builder clearCheck() {
            this.bitField0_ &= -17;
            this.check_ = 1;
            this.onChanged();
            return this;
         }

         private void ensureColumnIsMutable() {
            if ((this.bitField0_ & 32) == 0) {
               this.column_ = new LazyStringArrayList(this.column_);
               this.bitField0_ |= 32;
            }
         }

         public ProtocolStringList getColumnList() {
            return this.column_.getUnmodifiableView();
         }

         @Override
         public int getColumnCount() {
            return this.column_.size();
         }

         @Override
         public String getColumn(int index) {
            return this.column_.get(index);
         }

         @Override
         public ByteString getColumnBytes(int index) {
            return this.column_.getByteString(index);
         }

         public MysqlxCrud.CreateView.Builder setColumn(int index, String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.set(index, value);
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.CreateView.Builder addColumn(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.add(value);
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.CreateView.Builder addAllColumn(Iterable<String> values) {
            this.ensureColumnIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.column_);
            this.onChanged();
            return this;
         }

         public MysqlxCrud.CreateView.Builder clearColumn() {
            this.column_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= -33;
            this.onChanged();
            return this;
         }

         public MysqlxCrud.CreateView.Builder addColumnBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.add(value);
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasStmt() {
            return (this.bitField0_ & 64) != 0;
         }

         @Override
         public MysqlxCrud.Find getStmt() {
            if (this.stmtBuilder_ == null) {
               return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
            } else {
               return this.stmtBuilder_.getMessage();
            }
         }

         public MysqlxCrud.CreateView.Builder setStmt(MysqlxCrud.Find value) {
            if (this.stmtBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.stmt_ = value;
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public MysqlxCrud.CreateView.Builder setStmt(MysqlxCrud.Find.Builder builderForValue) {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = builderForValue.build();
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 64;
            return this;
         }

         public MysqlxCrud.CreateView.Builder mergeStmt(MysqlxCrud.Find value) {
            if (this.stmtBuilder_ == null) {
               if ((this.bitField0_ & 64) != 0 && this.stmt_ != null && this.stmt_ != MysqlxCrud.Find.getDefaultInstance()) {
                  this.stmt_ = MysqlxCrud.Find.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
               } else {
                  this.stmt_ = value;
               }

               this.onChanged();
            } else {
               this.stmtBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public MysqlxCrud.CreateView.Builder clearStmt() {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
               this.onChanged();
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -65;
            return this;
         }

         public MysqlxCrud.Find.Builder getStmtBuilder() {
            this.bitField0_ |= 64;
            this.onChanged();
            return this.getStmtFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.FindOrBuilder getStmtOrBuilder() {
            if (this.stmtBuilder_ != null) {
               return this.stmtBuilder_.getMessageOrBuilder();
            } else {
               return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> getStmtFieldBuilder() {
            if (this.stmtBuilder_ == null) {
               this.stmtBuilder_ = new SingleFieldBuilderV3<>(this.getStmt(), this.getParentForChildren(), this.isClean());
               this.stmt_ = null;
            }

            return this.stmtBuilder_;
         }

         @Override
         public boolean hasReplaceExisting() {
            return (this.bitField0_ & 128) != 0;
         }

         @Override
         public boolean getReplaceExisting() {
            return this.replaceExisting_;
         }

         public MysqlxCrud.CreateView.Builder setReplaceExisting(boolean value) {
            this.bitField0_ |= 128;
            this.replaceExisting_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxCrud.CreateView.Builder clearReplaceExisting() {
            this.bitField0_ &= -129;
            this.replaceExisting_ = false;
            this.onChanged();
            return this;
         }

         public final MysqlxCrud.CreateView.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.CreateView.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.CreateView.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.CreateView.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface CreateViewOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      MysqlxCrud.Collection getCollection();

      MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDefiner();

      String getDefiner();

      ByteString getDefinerBytes();

      boolean hasAlgorithm();

      MysqlxCrud.ViewAlgorithm getAlgorithm();

      boolean hasSecurity();

      MysqlxCrud.ViewSqlSecurity getSecurity();

      boolean hasCheck();

      MysqlxCrud.ViewCheckOption getCheck();

      List<String> getColumnList();

      int getColumnCount();

      String getColumn(int var1);

      ByteString getColumnBytes(int var1);

      boolean hasStmt();

      MysqlxCrud.Find getStmt();

      MysqlxCrud.FindOrBuilder getStmtOrBuilder();

      boolean hasReplaceExisting();

      boolean getReplaceExisting();
   }

   public static enum DataModel implements ProtocolMessageEnum {
      DOCUMENT(1),
      TABLE(2);

      public static final int DOCUMENT_VALUE = 1;
      public static final int TABLE_VALUE = 2;
      private static final Internal.EnumLiteMap<MysqlxCrud.DataModel> internalValueMap = new Internal.EnumLiteMap<MysqlxCrud.DataModel>() {
         public MysqlxCrud.DataModel findValueByNumber(int number) {
            return MysqlxCrud.DataModel.forNumber(number);
         }
      };
      private static final MysqlxCrud.DataModel[] VALUES = values();
      private final int value;

      @Override
      public final int getNumber() {
         return this.value;
      }

      @Deprecated
      public static MysqlxCrud.DataModel valueOf(int value) {
         return forNumber(value);
      }

      public static MysqlxCrud.DataModel forNumber(int value) {
         switch (value) {
            case 1:
               return DOCUMENT;
            case 2:
               return TABLE;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<MysqlxCrud.DataModel> internalGetValueMap() {
         return internalValueMap;
      }

      @Override
      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return getDescriptor().getValues().get(this.ordinal());
      }

      @Override
      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return MysqlxCrud.getDescriptor().getEnumTypes().get(0);
      }

      public static MysqlxCrud.DataModel valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private DataModel(int value) {
         this.value = value;
      }
   }

   public static final class Delete extends GeneratedMessageV3 implements MysqlxCrud.DeleteOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 1;
      private MysqlxCrud.Collection collection_;
      public static final int DATA_MODEL_FIELD_NUMBER = 2;
      private int dataModel_;
      public static final int CRITERIA_FIELD_NUMBER = 3;
      private MysqlxExpr.Expr criteria_;
      public static final int LIMIT_FIELD_NUMBER = 4;
      private MysqlxCrud.Limit limit_;
      public static final int ORDER_FIELD_NUMBER = 5;
      private List<MysqlxCrud.Order> order_;
      public static final int ARGS_FIELD_NUMBER = 6;
      private List<MysqlxDatatypes.Scalar> args_;
      public static final int LIMIT_EXPR_FIELD_NUMBER = 7;
      private MysqlxCrud.LimitExpr limitExpr_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.Delete DEFAULT_INSTANCE = new MysqlxCrud.Delete();
      @Deprecated
      public static final Parser<MysqlxCrud.Delete> PARSER = new AbstractParser<MysqlxCrud.Delete>() {
         public MysqlxCrud.Delete parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.Delete(input, extensionRegistry);
         }
      };

      private Delete(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Delete() {
         this.dataModel_ = 1;
         this.order_ = Collections.emptyList();
         this.args_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.Delete();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Delete(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxCrud.Collection.Builder subBuilderxxx = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilderxxx = this.collection_.toBuilder();
                        }

                        this.collection_ = input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilderxxx != null) {
                           subBuilderxxx.mergeFrom(this.collection_);
                           this.collection_ = subBuilderxxx.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        int rawValue = input.readEnum();
                        MysqlxCrud.DataModel value = MysqlxCrud.DataModel.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(2, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.dataModel_ = rawValue;
                        }
                        break;
                     case 26:
                        MysqlxExpr.Expr.Builder subBuilderxx = null;
                        if ((this.bitField0_ & 4) != 0) {
                           subBuilderxx = this.criteria_.toBuilder();
                        }

                        this.criteria_ = input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilderxx != null) {
                           subBuilderxx.mergeFrom(this.criteria_);
                           this.criteria_ = subBuilderxx.buildPartial();
                        }

                        this.bitField0_ |= 4;
                        break;
                     case 34:
                        MysqlxCrud.Limit.Builder subBuilderx = null;
                        if ((this.bitField0_ & 8) != 0) {
                           subBuilderx = this.limit_.toBuilder();
                        }

                        this.limit_ = input.readMessage(MysqlxCrud.Limit.PARSER, extensionRegistry);
                        if (subBuilderx != null) {
                           subBuilderx.mergeFrom(this.limit_);
                           this.limit_ = subBuilderx.buildPartial();
                        }

                        this.bitField0_ |= 8;
                        break;
                     case 42:
                        if ((mutable_bitField0_ & 16) == 0) {
                           this.order_ = new ArrayList<>();
                           mutable_bitField0_ |= 16;
                        }

                        this.order_.add(input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry));
                        break;
                     case 50:
                        if ((mutable_bitField0_ & 32) == 0) {
                           this.args_ = new ArrayList<>();
                           mutable_bitField0_ |= 32;
                        }

                        this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry));
                        break;
                     case 58:
                        MysqlxCrud.LimitExpr.Builder subBuilder = null;
                        if ((this.bitField0_ & 16) != 0) {
                           subBuilder = this.limitExpr_.toBuilder();
                        }

                        this.limitExpr_ = input.readMessage(MysqlxCrud.LimitExpr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.limitExpr_);
                           this.limitExpr_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 16;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw new InvalidProtocolBufferException(var14).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 16) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
               }

               if ((mutable_bitField0_ & 32) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.Delete.class, MysqlxCrud.Delete.Builder.class);
      }

      @Override
      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxCrud.Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public boolean hasDataModel() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public MysqlxCrud.DataModel getDataModel() {
         MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
         return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
      }

      @Override
      public boolean hasCriteria() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public MysqlxExpr.Expr getCriteria() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      @Override
      public boolean hasLimit() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public MysqlxCrud.Limit getLimit() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      @Override
      public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      @Override
      public List<MysqlxCrud.Order> getOrderList() {
         return this.order_;
      }

      @Override
      public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
         return this.order_;
      }

      @Override
      public int getOrderCount() {
         return this.order_.size();
      }

      @Override
      public MysqlxCrud.Order getOrder(int index) {
         return this.order_.get(index);
      }

      @Override
      public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
         return this.order_.get(index);
      }

      @Override
      public List<MysqlxDatatypes.Scalar> getArgsList() {
         return this.args_;
      }

      @Override
      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
         return this.args_;
      }

      @Override
      public int getArgsCount() {
         return this.args_.size();
      }

      @Override
      public MysqlxDatatypes.Scalar getArgs(int index) {
         return this.args_.get(index);
      }

      @Override
      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
         return this.args_.get(index);
      }

      @Override
      public boolean hasLimitExpr() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public MysqlxCrud.LimitExpr getLimitExpr() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      @Override
      public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            for (int i = 0; i < this.getOrderCount(); i++) {
               if (!this.getOrder(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for (int ix = 0; ix < this.getArgsCount(); ix++) {
               if (!this.getArgs(ix).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            if (this.hasLimitExpr() && !this.getLimitExpr().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else {
               this.memoizedIsInitialized = 1;
               return true;
            }
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.dataModel_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeMessage(3, this.getCriteria());
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeMessage(4, this.getLimit());
         }

         for (int i = 0; i < this.order_.size(); i++) {
            output.writeMessage(5, this.order_.get(i));
         }

         for (int i = 0; i < this.args_.size(); i++) {
            output.writeMessage(6, this.args_.get(i));
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeMessage(7, this.getLimitExpr());
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.dataModel_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeMessageSize(3, this.getCriteria());
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeMessageSize(4, this.getLimit());
            }

            for (int i = 0; i < this.order_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(5, this.order_.get(i));
            }

            for (int i = 0; i < this.args_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(6, this.args_.get(i));
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeMessageSize(7, this.getLimitExpr());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.Delete)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.Delete other = (MysqlxCrud.Delete)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDataModel() != other.hasDataModel()) {
               return false;
            } else if (this.hasDataModel() && this.dataModel_ != other.dataModel_) {
               return false;
            } else if (this.hasCriteria() != other.hasCriteria()) {
               return false;
            } else if (this.hasCriteria() && !this.getCriteria().equals(other.getCriteria())) {
               return false;
            } else if (this.hasLimit() != other.hasLimit()) {
               return false;
            } else if (this.hasLimit() && !this.getLimit().equals(other.getLimit())) {
               return false;
            } else if (!this.getOrderList().equals(other.getOrderList())) {
               return false;
            } else if (!this.getArgsList().equals(other.getArgsList())) {
               return false;
            } else if (this.hasLimitExpr() != other.hasLimitExpr()) {
               return false;
            } else {
               return this.hasLimitExpr() && !this.getLimitExpr().equals(other.getLimitExpr()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDataModel()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.dataModel_;
            }

            if (this.hasCriteria()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getCriteria().hashCode();
            }

            if (this.hasLimit()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getLimit().hashCode();
            }

            if (this.getOrderCount() > 0) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getOrderList().hashCode();
            }

            if (this.getArgsCount() > 0) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getArgsList().hashCode();
            }

            if (this.hasLimitExpr()) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getLimitExpr().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.Delete parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Delete parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Delete parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Delete parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Delete parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Delete parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Delete parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Delete parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Delete parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Delete parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Delete parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Delete parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.Delete.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.Delete.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.Delete.Builder newBuilder(MysqlxCrud.Delete prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.Delete.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.Delete.Builder() : new MysqlxCrud.Delete.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.Delete.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.Delete.Builder(parent);
      }

      public static MysqlxCrud.Delete getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.Delete> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.Delete> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.Delete getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.Delete.Builder> implements MysqlxCrud.DeleteOrBuilder {
         private int bitField0_;
         private MysqlxCrud.Collection collection_;
         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
         private int dataModel_ = 1;
         private MysqlxExpr.Expr criteria_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
         private MysqlxCrud.Limit limit_;
         private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> limitBuilder_;
         private List<MysqlxCrud.Order> order_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> orderBuilder_;
         private List<MysqlxDatatypes.Scalar> args_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
         private MysqlxCrud.LimitExpr limitExpr_;
         private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> limitExprBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.Delete.class, MysqlxCrud.Delete.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Delete.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getCriteriaFieldBuilder();
               this.getLimitFieldBuilder();
               this.getOrderFieldBuilder();
               this.getArgsFieldBuilder();
               this.getLimitExprFieldBuilder();
            }
         }

         public MysqlxCrud.Delete.Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.dataModel_ = 1;
            this.bitField0_ &= -3;
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -5;
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -9;
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -17;
            } else {
               this.orderBuilder_.clear();
            }

            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -33;
            } else {
               this.argsBuilder_.clear();
            }

            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -65;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor;
         }

         public MysqlxCrud.Delete getDefaultInstanceForType() {
            return MysqlxCrud.Delete.getDefaultInstance();
         }

         public MysqlxCrud.Delete build() {
            MysqlxCrud.Delete result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.Delete buildPartial() {
            MysqlxCrud.Delete result = new MysqlxCrud.Delete(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.dataModel_ = this.dataModel_;
            if ((from_bitField0_ & 4) != 0) {
               if (this.criteriaBuilder_ == null) {
                  result.criteria_ = this.criteria_;
               } else {
                  result.criteria_ = this.criteriaBuilder_.build();
               }

               to_bitField0_ |= 4;
            }

            if ((from_bitField0_ & 8) != 0) {
               if (this.limitBuilder_ == null) {
                  result.limit_ = this.limit_;
               } else {
                  result.limit_ = this.limitBuilder_.build();
               }

               to_bitField0_ |= 8;
            }

            if (this.orderBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
                  this.bitField0_ &= -17;
               }

               result.order_ = this.order_;
            } else {
               result.order_ = this.orderBuilder_.build();
            }

            if (this.argsBuilder_ == null) {
               if ((this.bitField0_ & 32) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
                  this.bitField0_ &= -33;
               }

               result.args_ = this.args_;
            } else {
               result.args_ = this.argsBuilder_.build();
            }

            if ((from_bitField0_ & 64) != 0) {
               if (this.limitExprBuilder_ == null) {
                  result.limitExpr_ = this.limitExpr_;
               } else {
                  result.limitExpr_ = this.limitExprBuilder_.build();
               }

               to_bitField0_ |= 16;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.Delete.Builder clone() {
            return (MysqlxCrud.Delete.Builder)super.clone();
         }

         public MysqlxCrud.Delete.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Delete.Builder)super.setField(field, value);
         }

         public MysqlxCrud.Delete.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.Delete.Builder)super.clearField(field);
         }

         public MysqlxCrud.Delete.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.Delete.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.Delete.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.Delete.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.Delete.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Delete.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.Delete.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.Delete) {
               return this.mergeFrom((MysqlxCrud.Delete)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.Delete.Builder mergeFrom(MysqlxCrud.Delete other) {
            if (other == MysqlxCrud.Delete.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDataModel()) {
                  this.setDataModel(other.getDataModel());
               }

               if (other.hasCriteria()) {
                  this.mergeCriteria(other.getCriteria());
               }

               if (other.hasLimit()) {
                  this.mergeLimit(other.getLimit());
               }

               if (this.orderBuilder_ == null) {
                  if (!other.order_.isEmpty()) {
                     if (this.order_.isEmpty()) {
                        this.order_ = other.order_;
                        this.bitField0_ &= -17;
                     } else {
                        this.ensureOrderIsMutable();
                        this.order_.addAll(other.order_);
                     }

                     this.onChanged();
                  }
               } else if (!other.order_.isEmpty()) {
                  if (this.orderBuilder_.isEmpty()) {
                     this.orderBuilder_.dispose();
                     this.orderBuilder_ = null;
                     this.order_ = other.order_;
                     this.bitField0_ &= -17;
                     this.orderBuilder_ = MysqlxCrud.Delete.alwaysUseFieldBuilders ? this.getOrderFieldBuilder() : null;
                  } else {
                     this.orderBuilder_.addAllMessages(other.order_);
                  }
               }

               if (this.argsBuilder_ == null) {
                  if (!other.args_.isEmpty()) {
                     if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= -33;
                     } else {
                        this.ensureArgsIsMutable();
                        this.args_.addAll(other.args_);
                     }

                     this.onChanged();
                  }
               } else if (!other.args_.isEmpty()) {
                  if (this.argsBuilder_.isEmpty()) {
                     this.argsBuilder_.dispose();
                     this.argsBuilder_ = null;
                     this.args_ = other.args_;
                     this.bitField0_ &= -33;
                     this.argsBuilder_ = MysqlxCrud.Delete.alwaysUseFieldBuilders ? this.getArgsFieldBuilder() : null;
                  } else {
                     this.argsBuilder_.addAllMessages(other.args_);
                  }
               }

               if (other.hasLimitExpr()) {
                  this.mergeLimitExpr(other.getLimitExpr());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else if (!this.getCollection().isInitialized()) {
               return false;
            } else if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
               return false;
            } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
               return false;
            } else {
               for (int i = 0; i < this.getOrderCount(); i++) {
                  if (!this.getOrder(i).isInitialized()) {
                     return false;
                  }
               }

               for (int ix = 0; ix < this.getArgsCount(); ix++) {
                  if (!this.getArgs(ix).isInitialized()) {
                     return false;
                  }
               }

               return !this.hasLimitExpr() || this.getLimitExpr().isInitialized();
            }
         }

         public MysqlxCrud.Delete.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.Delete parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.Delete.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.Delete)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxCrud.Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return this.collectionBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Delete.Builder setCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Delete.Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Delete.Builder mergeCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Delete.Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxCrud.Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getCollectionFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3<>(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         @Override
         public boolean hasDataModel() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxCrud.DataModel getDataModel() {
            MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
            return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
         }

         public MysqlxCrud.Delete.Builder setDataModel(MysqlxCrud.DataModel value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.dataModel_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Delete.Builder clearDataModel() {
            this.bitField0_ &= -3;
            this.dataModel_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasCriteria() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public MysqlxExpr.Expr getCriteria() {
            if (this.criteriaBuilder_ == null) {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            } else {
               return this.criteriaBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Delete.Builder setCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.criteria_ = value;
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public MysqlxCrud.Delete.Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = builderForValue.build();
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 4;
            return this;
         }

         public MysqlxCrud.Delete.Builder mergeCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial();
               } else {
                  this.criteria_ = value;
               }

               this.onChanged();
            } else {
               this.criteriaBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public MysqlxCrud.Delete.Builder clearCriteria() {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
               this.onChanged();
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -5;
            return this;
         }

         public MysqlxExpr.Expr.Builder getCriteriaBuilder() {
            this.bitField0_ |= 4;
            this.onChanged();
            return this.getCriteriaFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
            if (this.criteriaBuilder_ != null) {
               return this.criteriaBuilder_.getMessageOrBuilder();
            } else {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() {
            if (this.criteriaBuilder_ == null) {
               this.criteriaBuilder_ = new SingleFieldBuilderV3<>(this.getCriteria(), this.getParentForChildren(), this.isClean());
               this.criteria_ = null;
            }

            return this.criteriaBuilder_;
         }

         @Override
         public boolean hasLimit() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public MysqlxCrud.Limit getLimit() {
            if (this.limitBuilder_ == null) {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            } else {
               return this.limitBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Delete.Builder setLimit(MysqlxCrud.Limit value) {
            if (this.limitBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limit_ = value;
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public MysqlxCrud.Delete.Builder setLimit(MysqlxCrud.Limit.Builder builderForValue) {
            if (this.limitBuilder_ == null) {
               this.limit_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 8;
            return this;
         }

         public MysqlxCrud.Delete.Builder mergeLimit(MysqlxCrud.Limit value) {
            if (this.limitBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) {
                  this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial();
               } else {
                  this.limit_ = value;
               }

               this.onChanged();
            } else {
               this.limitBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public MysqlxCrud.Delete.Builder clearLimit() {
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
               this.onChanged();
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -9;
            return this;
         }

         public MysqlxCrud.Limit.Builder getLimitBuilder() {
            this.bitField0_ |= 8;
            this.onChanged();
            return this.getLimitFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
            if (this.limitBuilder_ != null) {
               return this.limitBuilder_.getMessageOrBuilder();
            } else {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> getLimitFieldBuilder() {
            if (this.limitBuilder_ == null) {
               this.limitBuilder_ = new SingleFieldBuilderV3<>(this.getLimit(), this.getParentForChildren(), this.isClean());
               this.limit_ = null;
            }

            return this.limitBuilder_;
         }

         private void ensureOrderIsMutable() {
            if ((this.bitField0_ & 16) == 0) {
               this.order_ = new ArrayList<>(this.order_);
               this.bitField0_ |= 16;
            }
         }

         @Override
         public List<MysqlxCrud.Order> getOrderList() {
            return this.orderBuilder_ == null ? Collections.unmodifiableList(this.order_) : this.orderBuilder_.getMessageList();
         }

         @Override
         public int getOrderCount() {
            return this.orderBuilder_ == null ? this.order_.size() : this.orderBuilder_.getCount();
         }

         @Override
         public MysqlxCrud.Order getOrder(int index) {
            return this.orderBuilder_ == null ? this.order_.get(index) : this.orderBuilder_.getMessage(index);
         }

         public MysqlxCrud.Delete.Builder setOrder(int index, MysqlxCrud.Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.set(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder setOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder addOrder(MysqlxCrud.Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder addOrder(int index, MysqlxCrud.Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder addOrder(MysqlxCrud.Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder addOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder addAllOrder(Iterable<? extends MysqlxCrud.Order> values) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.order_);
               this.onChanged();
            } else {
               this.orderBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder clearOrder() {
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -17;
               this.onChanged();
            } else {
               this.orderBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder removeOrder(int index) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.remove(index);
               this.onChanged();
            } else {
               this.orderBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxCrud.Order.Builder getOrderBuilder(int index) {
            return this.getOrderFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
            return this.orderBuilder_ == null ? this.order_.get(index) : this.orderBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
            return this.orderBuilder_ != null ? this.orderBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.order_);
         }

         public MysqlxCrud.Order.Builder addOrderBuilder() {
            return this.getOrderFieldBuilder().addBuilder(MysqlxCrud.Order.getDefaultInstance());
         }

         public MysqlxCrud.Order.Builder addOrderBuilder(int index) {
            return this.getOrderFieldBuilder().addBuilder(index, MysqlxCrud.Order.getDefaultInstance());
         }

         public List<MysqlxCrud.Order.Builder> getOrderBuilderList() {
            return this.getOrderFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> getOrderFieldBuilder() {
            if (this.orderBuilder_ == null) {
               this.orderBuilder_ = new RepeatedFieldBuilderV3<>(this.order_, (this.bitField0_ & 16) != 0, this.getParentForChildren(), this.isClean());
               this.order_ = null;
            }

            return this.orderBuilder_;
         }

         private void ensureArgsIsMutable() {
            if ((this.bitField0_ & 32) == 0) {
               this.args_ = new ArrayList<>(this.args_);
               this.bitField0_ |= 32;
            }
         }

         @Override
         public List<MysqlxDatatypes.Scalar> getArgsList() {
            return this.argsBuilder_ == null ? Collections.unmodifiableList(this.args_) : this.argsBuilder_.getMessageList();
         }

         @Override
         public int getArgsCount() {
            return this.argsBuilder_ == null ? this.args_.size() : this.argsBuilder_.getCount();
         }

         @Override
         public MysqlxDatatypes.Scalar getArgs(int index) {
            return this.argsBuilder_ == null ? this.args_.get(index) : this.argsBuilder_.getMessage(index);
         }

         public MysqlxCrud.Delete.Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.set(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder addArgs(MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.args_);
               this.onChanged();
            } else {
               this.argsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder clearArgs() {
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -33;
               this.onChanged();
            } else {
               this.argsBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Delete.Builder removeArgs(int index) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.remove(index);
               this.onChanged();
            } else {
               this.argsBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
            return this.getArgsFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
            return this.argsBuilder_ == null ? this.args_.get(index) : this.argsBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
            return this.argsBuilder_ != null ? this.argsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.args_);
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
            return this.getArgsFieldBuilder().addBuilder(MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
            return this.getArgsFieldBuilder().addBuilder(index, MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
            return this.getArgsFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
            if (this.argsBuilder_ == null) {
               this.argsBuilder_ = new RepeatedFieldBuilderV3<>(this.args_, (this.bitField0_ & 32) != 0, this.getParentForChildren(), this.isClean());
               this.args_ = null;
            }

            return this.argsBuilder_;
         }

         @Override
         public boolean hasLimitExpr() {
            return (this.bitField0_ & 64) != 0;
         }

         @Override
         public MysqlxCrud.LimitExpr getLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            } else {
               return this.limitExprBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Delete.Builder setLimitExpr(MysqlxCrud.LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limitExpr_ = value;
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public MysqlxCrud.Delete.Builder setLimitExpr(MysqlxCrud.LimitExpr.Builder builderForValue) {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 64;
            return this;
         }

         public MysqlxCrud.Delete.Builder mergeLimitExpr(MysqlxCrud.LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if ((this.bitField0_ & 64) != 0 && this.limitExpr_ != null && this.limitExpr_ != MysqlxCrud.LimitExpr.getDefaultInstance()) {
                  this.limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
               } else {
                  this.limitExpr_ = value;
               }

               this.onChanged();
            } else {
               this.limitExprBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public MysqlxCrud.Delete.Builder clearLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
               this.onChanged();
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -65;
            return this;
         }

         public MysqlxCrud.LimitExpr.Builder getLimitExprBuilder() {
            this.bitField0_ |= 64;
            this.onChanged();
            return this.getLimitExprFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
            if (this.limitExprBuilder_ != null) {
               return this.limitExprBuilder_.getMessageOrBuilder();
            } else {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> getLimitExprFieldBuilder() {
            if (this.limitExprBuilder_ == null) {
               this.limitExprBuilder_ = new SingleFieldBuilderV3<>(this.getLimitExpr(), this.getParentForChildren(), this.isClean());
               this.limitExpr_ = null;
            }

            return this.limitExprBuilder_;
         }

         public final MysqlxCrud.Delete.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Delete.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.Delete.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Delete.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface DeleteOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      MysqlxCrud.Collection getCollection();

      MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDataModel();

      MysqlxCrud.DataModel getDataModel();

      boolean hasCriteria();

      MysqlxExpr.Expr getCriteria();

      MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();

      boolean hasLimit();

      MysqlxCrud.Limit getLimit();

      MysqlxCrud.LimitOrBuilder getLimitOrBuilder();

      List<MysqlxCrud.Order> getOrderList();

      MysqlxCrud.Order getOrder(int var1);

      int getOrderCount();

      List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList();

      MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int var1);

      List<MysqlxDatatypes.Scalar> getArgsList();

      MysqlxDatatypes.Scalar getArgs(int var1);

      int getArgsCount();

      List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();

      MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int var1);

      boolean hasLimitExpr();

      MysqlxCrud.LimitExpr getLimitExpr();

      MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder();
   }

   public static final class DropView extends GeneratedMessageV3 implements MysqlxCrud.DropViewOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 1;
      private MysqlxCrud.Collection collection_;
      public static final int IF_EXISTS_FIELD_NUMBER = 2;
      private boolean ifExists_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.DropView DEFAULT_INSTANCE = new MysqlxCrud.DropView();
      @Deprecated
      public static final Parser<MysqlxCrud.DropView> PARSER = new AbstractParser<MysqlxCrud.DropView>() {
         public MysqlxCrud.DropView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.DropView(input, extensionRegistry);
         }
      };

      private DropView(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private DropView() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.DropView();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private DropView(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxCrud.Collection.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.collection_.toBuilder();
                        }

                        this.collection_ = input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.collection_);
                           this.collection_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        this.bitField0_ |= 2;
                        this.ifExists_ = input.readBool();
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.DropView.class, MysqlxCrud.DropView.Builder.class);
      }

      @Override
      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxCrud.Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public boolean hasIfExists() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public boolean getIfExists() {
         return this.ifExists_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeBool(2, this.ifExists_);
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeBoolSize(2, this.ifExists_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.DropView)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.DropView other = (MysqlxCrud.DropView)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasIfExists() != other.hasIfExists()) {
               return false;
            } else {
               return this.hasIfExists() && this.getIfExists() != other.getIfExists() ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasIfExists()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + Internal.hashBoolean(this.getIfExists());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.DropView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.DropView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.DropView parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.DropView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.DropView parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.DropView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.DropView parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.DropView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.DropView parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.DropView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.DropView parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.DropView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.DropView.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.DropView.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.DropView.Builder newBuilder(MysqlxCrud.DropView prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.DropView.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.DropView.Builder() : new MysqlxCrud.DropView.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.DropView.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.DropView.Builder(parent);
      }

      public static MysqlxCrud.DropView getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.DropView> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.DropView> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.DropView getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.DropView.Builder> implements MysqlxCrud.DropViewOrBuilder {
         private int bitField0_;
         private MysqlxCrud.Collection collection_;
         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
         private boolean ifExists_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.DropView.class, MysqlxCrud.DropView.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.DropView.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
            }
         }

         public MysqlxCrud.DropView.Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.ifExists_ = false;
            this.bitField0_ &= -3;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor;
         }

         public MysqlxCrud.DropView getDefaultInstanceForType() {
            return MysqlxCrud.DropView.getDefaultInstance();
         }

         public MysqlxCrud.DropView build() {
            MysqlxCrud.DropView result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.DropView buildPartial() {
            MysqlxCrud.DropView result = new MysqlxCrud.DropView(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               result.ifExists_ = this.ifExists_;
               to_bitField0_ |= 2;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.DropView.Builder clone() {
            return (MysqlxCrud.DropView.Builder)super.clone();
         }

         public MysqlxCrud.DropView.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.DropView.Builder)super.setField(field, value);
         }

         public MysqlxCrud.DropView.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.DropView.Builder)super.clearField(field);
         }

         public MysqlxCrud.DropView.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.DropView.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.DropView.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.DropView.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.DropView.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.DropView.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.DropView.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.DropView) {
               return this.mergeFrom((MysqlxCrud.DropView)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.DropView.Builder mergeFrom(MysqlxCrud.DropView other) {
            if (other == MysqlxCrud.DropView.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasIfExists()) {
                  this.setIfExists(other.getIfExists());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return !this.hasCollection() ? false : this.getCollection().isInitialized();
         }

         public MysqlxCrud.DropView.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.DropView parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.DropView.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.DropView)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxCrud.Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return this.collectionBuilder_.getMessage();
            }
         }

         public MysqlxCrud.DropView.Builder setCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.DropView.Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.DropView.Builder mergeCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.DropView.Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxCrud.Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getCollectionFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3<>(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         @Override
         public boolean hasIfExists() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public boolean getIfExists() {
            return this.ifExists_;
         }

         public MysqlxCrud.DropView.Builder setIfExists(boolean value) {
            this.bitField0_ |= 2;
            this.ifExists_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxCrud.DropView.Builder clearIfExists() {
            this.bitField0_ &= -3;
            this.ifExists_ = false;
            this.onChanged();
            return this;
         }

         public final MysqlxCrud.DropView.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.DropView.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.DropView.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.DropView.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface DropViewOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      MysqlxCrud.Collection getCollection();

      MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();

      boolean hasIfExists();

      boolean getIfExists();
   }

   public static final class Find extends GeneratedMessageV3 implements MysqlxCrud.FindOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 2;
      private MysqlxCrud.Collection collection_;
      public static final int DATA_MODEL_FIELD_NUMBER = 3;
      private int dataModel_;
      public static final int PROJECTION_FIELD_NUMBER = 4;
      private List<MysqlxCrud.Projection> projection_;
      public static final int ARGS_FIELD_NUMBER = 11;
      private List<MysqlxDatatypes.Scalar> args_;
      public static final int CRITERIA_FIELD_NUMBER = 5;
      private MysqlxExpr.Expr criteria_;
      public static final int LIMIT_FIELD_NUMBER = 6;
      private MysqlxCrud.Limit limit_;
      public static final int ORDER_FIELD_NUMBER = 7;
      private List<MysqlxCrud.Order> order_;
      public static final int GROUPING_FIELD_NUMBER = 8;
      private List<MysqlxExpr.Expr> grouping_;
      public static final int GROUPING_CRITERIA_FIELD_NUMBER = 9;
      private MysqlxExpr.Expr groupingCriteria_;
      public static final int LOCKING_FIELD_NUMBER = 12;
      private int locking_;
      public static final int LOCKING_OPTIONS_FIELD_NUMBER = 13;
      private int lockingOptions_;
      public static final int LIMIT_EXPR_FIELD_NUMBER = 14;
      private MysqlxCrud.LimitExpr limitExpr_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.Find DEFAULT_INSTANCE = new MysqlxCrud.Find();
      @Deprecated
      public static final Parser<MysqlxCrud.Find> PARSER = new AbstractParser<MysqlxCrud.Find>() {
         public MysqlxCrud.Find parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.Find(input, extensionRegistry);
         }
      };

      private Find(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Find() {
         this.dataModel_ = 1;
         this.projection_ = Collections.emptyList();
         this.args_ = Collections.emptyList();
         this.order_ = Collections.emptyList();
         this.grouping_ = Collections.emptyList();
         this.locking_ = 1;
         this.lockingOptions_ = 1;
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.Find();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Find(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 18:
                        MysqlxCrud.Collection.Builder subBuilderxxxx = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilderxxxx = this.collection_.toBuilder();
                        }

                        this.collection_ = input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilderxxxx != null) {
                           subBuilderxxxx.mergeFrom(this.collection_);
                           this.collection_ = subBuilderxxxx.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 24:
                        int rawValuexx = input.readEnum();
                        MysqlxCrud.DataModel valuexx = MysqlxCrud.DataModel.valueOf(rawValuexx);
                        if (valuexx == null) {
                           unknownFields.mergeVarintField(3, rawValuexx);
                        } else {
                           this.bitField0_ |= 2;
                           this.dataModel_ = rawValuexx;
                        }
                        break;
                     case 34:
                        if ((mutable_bitField0_ & 4) == 0) {
                           this.projection_ = new ArrayList<>();
                           mutable_bitField0_ |= 4;
                        }

                        this.projection_.add(input.readMessage(MysqlxCrud.Projection.PARSER, extensionRegistry));
                        break;
                     case 42:
                        MysqlxExpr.Expr.Builder subBuilderxxx = null;
                        if ((this.bitField0_ & 4) != 0) {
                           subBuilderxxx = this.criteria_.toBuilder();
                        }

                        this.criteria_ = input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilderxxx != null) {
                           subBuilderxxx.mergeFrom(this.criteria_);
                           this.criteria_ = subBuilderxxx.buildPartial();
                        }

                        this.bitField0_ |= 4;
                        break;
                     case 50:
                        MysqlxCrud.Limit.Builder subBuilderxx = null;
                        if ((this.bitField0_ & 8) != 0) {
                           subBuilderxx = this.limit_.toBuilder();
                        }

                        this.limit_ = input.readMessage(MysqlxCrud.Limit.PARSER, extensionRegistry);
                        if (subBuilderxx != null) {
                           subBuilderxx.mergeFrom(this.limit_);
                           this.limit_ = subBuilderxx.buildPartial();
                        }

                        this.bitField0_ |= 8;
                        break;
                     case 58:
                        if ((mutable_bitField0_ & 64) == 0) {
                           this.order_ = new ArrayList<>();
                           mutable_bitField0_ |= 64;
                        }

                        this.order_.add(input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry));
                        break;
                     case 66:
                        if ((mutable_bitField0_ & 128) == 0) {
                           this.grouping_ = new ArrayList<>();
                           mutable_bitField0_ |= 128;
                        }

                        this.grouping_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry));
                        break;
                     case 74:
                        MysqlxExpr.Expr.Builder subBuilderx = null;
                        if ((this.bitField0_ & 16) != 0) {
                           subBuilderx = this.groupingCriteria_.toBuilder();
                        }

                        this.groupingCriteria_ = input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilderx != null) {
                           subBuilderx.mergeFrom(this.groupingCriteria_);
                           this.groupingCriteria_ = subBuilderx.buildPartial();
                        }

                        this.bitField0_ |= 16;
                        break;
                     case 90:
                        if ((mutable_bitField0_ & 8) == 0) {
                           this.args_ = new ArrayList<>();
                           mutable_bitField0_ |= 8;
                        }

                        this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry));
                        break;
                     case 96:
                        int rawValuex = input.readEnum();
                        MysqlxCrud.Find.RowLock valuex = MysqlxCrud.Find.RowLock.valueOf(rawValuex);
                        if (valuex == null) {
                           unknownFields.mergeVarintField(12, rawValuex);
                        } else {
                           this.bitField0_ |= 32;
                           this.locking_ = rawValuex;
                        }
                        break;
                     case 104:
                        int rawValue = input.readEnum();
                        MysqlxCrud.Find.RowLockOptions value = MysqlxCrud.Find.RowLockOptions.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(13, rawValue);
                        } else {
                           this.bitField0_ |= 64;
                           this.lockingOptions_ = rawValue;
                        }
                        break;
                     case 114:
                        MysqlxCrud.LimitExpr.Builder subBuilder = null;
                        if ((this.bitField0_ & 128) != 0) {
                           subBuilder = this.limitExpr_.toBuilder();
                        }

                        this.limitExpr_ = input.readMessage(MysqlxCrud.LimitExpr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.limitExpr_);
                           this.limitExpr_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 128;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw new InvalidProtocolBufferException(var14).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 4) != 0) {
                  this.projection_ = Collections.unmodifiableList(this.projection_);
               }

               if ((mutable_bitField0_ & 64) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
               }

               if ((mutable_bitField0_ & 128) != 0) {
                  this.grouping_ = Collections.unmodifiableList(this.grouping_);
               }

               if ((mutable_bitField0_ & 8) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Find_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.Find.class, MysqlxCrud.Find.Builder.class);
      }

      @Override
      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxCrud.Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public boolean hasDataModel() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public MysqlxCrud.DataModel getDataModel() {
         MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
         return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
      }

      @Override
      public List<MysqlxCrud.Projection> getProjectionList() {
         return this.projection_;
      }

      @Override
      public List<? extends MysqlxCrud.ProjectionOrBuilder> getProjectionOrBuilderList() {
         return this.projection_;
      }

      @Override
      public int getProjectionCount() {
         return this.projection_.size();
      }

      @Override
      public MysqlxCrud.Projection getProjection(int index) {
         return this.projection_.get(index);
      }

      @Override
      public MysqlxCrud.ProjectionOrBuilder getProjectionOrBuilder(int index) {
         return this.projection_.get(index);
      }

      @Override
      public List<MysqlxDatatypes.Scalar> getArgsList() {
         return this.args_;
      }

      @Override
      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
         return this.args_;
      }

      @Override
      public int getArgsCount() {
         return this.args_.size();
      }

      @Override
      public MysqlxDatatypes.Scalar getArgs(int index) {
         return this.args_.get(index);
      }

      @Override
      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
         return this.args_.get(index);
      }

      @Override
      public boolean hasCriteria() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public MysqlxExpr.Expr getCriteria() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      @Override
      public boolean hasLimit() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public MysqlxCrud.Limit getLimit() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      @Override
      public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      @Override
      public List<MysqlxCrud.Order> getOrderList() {
         return this.order_;
      }

      @Override
      public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
         return this.order_;
      }

      @Override
      public int getOrderCount() {
         return this.order_.size();
      }

      @Override
      public MysqlxCrud.Order getOrder(int index) {
         return this.order_.get(index);
      }

      @Override
      public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
         return this.order_.get(index);
      }

      @Override
      public List<MysqlxExpr.Expr> getGroupingList() {
         return this.grouping_;
      }

      @Override
      public List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList() {
         return this.grouping_;
      }

      @Override
      public int getGroupingCount() {
         return this.grouping_.size();
      }

      @Override
      public MysqlxExpr.Expr getGrouping(int index) {
         return this.grouping_.get(index);
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int index) {
         return this.grouping_.get(index);
      }

      @Override
      public boolean hasGroupingCriteria() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public MysqlxExpr.Expr getGroupingCriteria() {
         return this.groupingCriteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder() {
         return this.groupingCriteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;
      }

      @Override
      public boolean hasLocking() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public MysqlxCrud.Find.RowLock getLocking() {
         MysqlxCrud.Find.RowLock result = MysqlxCrud.Find.RowLock.valueOf(this.locking_);
         return result == null ? MysqlxCrud.Find.RowLock.SHARED_LOCK : result;
      }

      @Override
      public boolean hasLockingOptions() {
         return (this.bitField0_ & 64) != 0;
      }

      @Override
      public MysqlxCrud.Find.RowLockOptions getLockingOptions() {
         MysqlxCrud.Find.RowLockOptions result = MysqlxCrud.Find.RowLockOptions.valueOf(this.lockingOptions_);
         return result == null ? MysqlxCrud.Find.RowLockOptions.NOWAIT : result;
      }

      @Override
      public boolean hasLimitExpr() {
         return (this.bitField0_ & 128) != 0;
      }

      @Override
      public MysqlxCrud.LimitExpr getLimitExpr() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      @Override
      public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            for (int i = 0; i < this.getProjectionCount(); i++) {
               if (!this.getProjection(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for (int ix = 0; ix < this.getArgsCount(); ix++) {
               if (!this.getArgs(ix).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else {
               for (int ixx = 0; ixx < this.getOrderCount(); ixx++) {
                  if (!this.getOrder(ixx).isInitialized()) {
                     this.memoizedIsInitialized = 0;
                     return false;
                  }
               }

               for (int ixxx = 0; ixxx < this.getGroupingCount(); ixxx++) {
                  if (!this.getGrouping(ixxx).isInitialized()) {
                     this.memoizedIsInitialized = 0;
                     return false;
                  }
               }

               if (this.hasGroupingCriteria() && !this.getGroupingCriteria().isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               } else if (this.hasLimitExpr() && !this.getLimitExpr().isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               } else {
                  this.memoizedIsInitialized = 1;
                  return true;
               }
            }
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(2, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(3, this.dataModel_);
         }

         for (int i = 0; i < this.projection_.size(); i++) {
            output.writeMessage(4, this.projection_.get(i));
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeMessage(5, this.getCriteria());
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeMessage(6, this.getLimit());
         }

         for (int i = 0; i < this.order_.size(); i++) {
            output.writeMessage(7, this.order_.get(i));
         }

         for (int i = 0; i < this.grouping_.size(); i++) {
            output.writeMessage(8, this.grouping_.get(i));
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeMessage(9, this.getGroupingCriteria());
         }

         for (int i = 0; i < this.args_.size(); i++) {
            output.writeMessage(11, this.args_.get(i));
         }

         if ((this.bitField0_ & 32) != 0) {
            output.writeEnum(12, this.locking_);
         }

         if ((this.bitField0_ & 64) != 0) {
            output.writeEnum(13, this.lockingOptions_);
         }

         if ((this.bitField0_ & 128) != 0) {
            output.writeMessage(14, this.getLimitExpr());
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(2, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(3, this.dataModel_);
            }

            for (int i = 0; i < this.projection_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(4, this.projection_.get(i));
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeMessageSize(5, this.getCriteria());
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeMessageSize(6, this.getLimit());
            }

            for (int i = 0; i < this.order_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(7, this.order_.get(i));
            }

            for (int i = 0; i < this.grouping_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(8, this.grouping_.get(i));
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeMessageSize(9, this.getGroupingCriteria());
            }

            for (int i = 0; i < this.args_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(11, this.args_.get(i));
            }

            if ((this.bitField0_ & 32) != 0) {
               size += CodedOutputStream.computeEnumSize(12, this.locking_);
            }

            if ((this.bitField0_ & 64) != 0) {
               size += CodedOutputStream.computeEnumSize(13, this.lockingOptions_);
            }

            if ((this.bitField0_ & 128) != 0) {
               size += CodedOutputStream.computeMessageSize(14, this.getLimitExpr());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.Find)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.Find other = (MysqlxCrud.Find)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDataModel() != other.hasDataModel()) {
               return false;
            } else if (this.hasDataModel() && this.dataModel_ != other.dataModel_) {
               return false;
            } else if (!this.getProjectionList().equals(other.getProjectionList())) {
               return false;
            } else if (!this.getArgsList().equals(other.getArgsList())) {
               return false;
            } else if (this.hasCriteria() != other.hasCriteria()) {
               return false;
            } else if (this.hasCriteria() && !this.getCriteria().equals(other.getCriteria())) {
               return false;
            } else if (this.hasLimit() != other.hasLimit()) {
               return false;
            } else if (this.hasLimit() && !this.getLimit().equals(other.getLimit())) {
               return false;
            } else if (!this.getOrderList().equals(other.getOrderList())) {
               return false;
            } else if (!this.getGroupingList().equals(other.getGroupingList())) {
               return false;
            } else if (this.hasGroupingCriteria() != other.hasGroupingCriteria()) {
               return false;
            } else if (this.hasGroupingCriteria() && !this.getGroupingCriteria().equals(other.getGroupingCriteria())) {
               return false;
            } else if (this.hasLocking() != other.hasLocking()) {
               return false;
            } else if (this.hasLocking() && this.locking_ != other.locking_) {
               return false;
            } else if (this.hasLockingOptions() != other.hasLockingOptions()) {
               return false;
            } else if (this.hasLockingOptions() && this.lockingOptions_ != other.lockingOptions_) {
               return false;
            } else if (this.hasLimitExpr() != other.hasLimitExpr()) {
               return false;
            } else {
               return this.hasLimitExpr() && !this.getLimitExpr().equals(other.getLimitExpr()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDataModel()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.dataModel_;
            }

            if (this.getProjectionCount() > 0) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getProjectionList().hashCode();
            }

            if (this.getArgsCount() > 0) {
               hash = 37 * hash + 11;
               hash = 53 * hash + this.getArgsList().hashCode();
            }

            if (this.hasCriteria()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getCriteria().hashCode();
            }

            if (this.hasLimit()) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getLimit().hashCode();
            }

            if (this.getOrderCount() > 0) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getOrderList().hashCode();
            }

            if (this.getGroupingCount() > 0) {
               hash = 37 * hash + 8;
               hash = 53 * hash + this.getGroupingList().hashCode();
            }

            if (this.hasGroupingCriteria()) {
               hash = 37 * hash + 9;
               hash = 53 * hash + this.getGroupingCriteria().hashCode();
            }

            if (this.hasLocking()) {
               hash = 37 * hash + 12;
               hash = 53 * hash + this.locking_;
            }

            if (this.hasLockingOptions()) {
               hash = 37 * hash + 13;
               hash = 53 * hash + this.lockingOptions_;
            }

            if (this.hasLimitExpr()) {
               hash = 37 * hash + 14;
               hash = 53 * hash + this.getLimitExpr().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.Find parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Find parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Find parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Find parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Find parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Find parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Find parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Find parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Find parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Find parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Find parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Find parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.Find.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.Find.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.Find.Builder newBuilder(MysqlxCrud.Find prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.Find.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.Find.Builder() : new MysqlxCrud.Find.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.Find.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.Find.Builder(parent);
      }

      public static MysqlxCrud.Find getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.Find> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.Find> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.Find getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.Find.Builder> implements MysqlxCrud.FindOrBuilder {
         private int bitField0_;
         private MysqlxCrud.Collection collection_;
         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
         private int dataModel_ = 1;
         private List<MysqlxCrud.Projection> projection_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxCrud.Projection, MysqlxCrud.Projection.Builder, MysqlxCrud.ProjectionOrBuilder> projectionBuilder_;
         private List<MysqlxDatatypes.Scalar> args_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
         private MysqlxExpr.Expr criteria_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
         private MysqlxCrud.Limit limit_;
         private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> limitBuilder_;
         private List<MysqlxCrud.Order> order_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> orderBuilder_;
         private List<MysqlxExpr.Expr> grouping_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> groupingBuilder_;
         private MysqlxExpr.Expr groupingCriteria_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> groupingCriteriaBuilder_;
         private int locking_ = 1;
         private int lockingOptions_ = 1;
         private MysqlxCrud.LimitExpr limitExpr_;
         private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> limitExprBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Find_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.Find.class, MysqlxCrud.Find.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Find.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getProjectionFieldBuilder();
               this.getArgsFieldBuilder();
               this.getCriteriaFieldBuilder();
               this.getLimitFieldBuilder();
               this.getOrderFieldBuilder();
               this.getGroupingFieldBuilder();
               this.getGroupingCriteriaFieldBuilder();
               this.getLimitExprFieldBuilder();
            }
         }

         public MysqlxCrud.Find.Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.dataModel_ = 1;
            this.bitField0_ &= -3;
            if (this.projectionBuilder_ == null) {
               this.projection_ = Collections.emptyList();
               this.bitField0_ &= -5;
            } else {
               this.projectionBuilder_.clear();
            }

            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -9;
            } else {
               this.argsBuilder_.clear();
            }

            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -17;
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -33;
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -65;
            } else {
               this.orderBuilder_.clear();
            }

            if (this.groupingBuilder_ == null) {
               this.grouping_ = Collections.emptyList();
               this.bitField0_ &= -129;
            } else {
               this.groupingBuilder_.clear();
            }

            if (this.groupingCriteriaBuilder_ == null) {
               this.groupingCriteria_ = null;
            } else {
               this.groupingCriteriaBuilder_.clear();
            }

            this.bitField0_ &= -257;
            this.locking_ = 1;
            this.bitField0_ &= -513;
            this.lockingOptions_ = 1;
            this.bitField0_ &= -1025;
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -2049;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor;
         }

         public MysqlxCrud.Find getDefaultInstanceForType() {
            return MysqlxCrud.Find.getDefaultInstance();
         }

         public MysqlxCrud.Find build() {
            MysqlxCrud.Find result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.Find buildPartial() {
            MysqlxCrud.Find result = new MysqlxCrud.Find(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.dataModel_ = this.dataModel_;
            if (this.projectionBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0) {
                  this.projection_ = Collections.unmodifiableList(this.projection_);
                  this.bitField0_ &= -5;
               }

               result.projection_ = this.projection_;
            } else {
               result.projection_ = this.projectionBuilder_.build();
            }

            if (this.argsBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
                  this.bitField0_ &= -9;
               }

               result.args_ = this.args_;
            } else {
               result.args_ = this.argsBuilder_.build();
            }

            if ((from_bitField0_ & 16) != 0) {
               if (this.criteriaBuilder_ == null) {
                  result.criteria_ = this.criteria_;
               } else {
                  result.criteria_ = this.criteriaBuilder_.build();
               }

               to_bitField0_ |= 4;
            }

            if ((from_bitField0_ & 32) != 0) {
               if (this.limitBuilder_ == null) {
                  result.limit_ = this.limit_;
               } else {
                  result.limit_ = this.limitBuilder_.build();
               }

               to_bitField0_ |= 8;
            }

            if (this.orderBuilder_ == null) {
               if ((this.bitField0_ & 64) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
                  this.bitField0_ &= -65;
               }

               result.order_ = this.order_;
            } else {
               result.order_ = this.orderBuilder_.build();
            }

            if (this.groupingBuilder_ == null) {
               if ((this.bitField0_ & 128) != 0) {
                  this.grouping_ = Collections.unmodifiableList(this.grouping_);
                  this.bitField0_ &= -129;
               }

               result.grouping_ = this.grouping_;
            } else {
               result.grouping_ = this.groupingBuilder_.build();
            }

            if ((from_bitField0_ & 256) != 0) {
               if (this.groupingCriteriaBuilder_ == null) {
                  result.groupingCriteria_ = this.groupingCriteria_;
               } else {
                  result.groupingCriteria_ = this.groupingCriteriaBuilder_.build();
               }

               to_bitField0_ |= 16;
            }

            if ((from_bitField0_ & 512) != 0) {
               to_bitField0_ |= 32;
            }

            result.locking_ = this.locking_;
            if ((from_bitField0_ & 1024) != 0) {
               to_bitField0_ |= 64;
            }

            result.lockingOptions_ = this.lockingOptions_;
            if ((from_bitField0_ & 2048) != 0) {
               if (this.limitExprBuilder_ == null) {
                  result.limitExpr_ = this.limitExpr_;
               } else {
                  result.limitExpr_ = this.limitExprBuilder_.build();
               }

               to_bitField0_ |= 128;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.Find.Builder clone() {
            return (MysqlxCrud.Find.Builder)super.clone();
         }

         public MysqlxCrud.Find.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Find.Builder)super.setField(field, value);
         }

         public MysqlxCrud.Find.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.Find.Builder)super.clearField(field);
         }

         public MysqlxCrud.Find.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.Find.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.Find.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.Find.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.Find.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Find.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.Find.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.Find) {
               return this.mergeFrom((MysqlxCrud.Find)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.Find.Builder mergeFrom(MysqlxCrud.Find other) {
            if (other == MysqlxCrud.Find.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDataModel()) {
                  this.setDataModel(other.getDataModel());
               }

               if (this.projectionBuilder_ == null) {
                  if (!other.projection_.isEmpty()) {
                     if (this.projection_.isEmpty()) {
                        this.projection_ = other.projection_;
                        this.bitField0_ &= -5;
                     } else {
                        this.ensureProjectionIsMutable();
                        this.projection_.addAll(other.projection_);
                     }

                     this.onChanged();
                  }
               } else if (!other.projection_.isEmpty()) {
                  if (this.projectionBuilder_.isEmpty()) {
                     this.projectionBuilder_.dispose();
                     this.projectionBuilder_ = null;
                     this.projection_ = other.projection_;
                     this.bitField0_ &= -5;
                     this.projectionBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? this.getProjectionFieldBuilder() : null;
                  } else {
                     this.projectionBuilder_.addAllMessages(other.projection_);
                  }
               }

               if (this.argsBuilder_ == null) {
                  if (!other.args_.isEmpty()) {
                     if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= -9;
                     } else {
                        this.ensureArgsIsMutable();
                        this.args_.addAll(other.args_);
                     }

                     this.onChanged();
                  }
               } else if (!other.args_.isEmpty()) {
                  if (this.argsBuilder_.isEmpty()) {
                     this.argsBuilder_.dispose();
                     this.argsBuilder_ = null;
                     this.args_ = other.args_;
                     this.bitField0_ &= -9;
                     this.argsBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? this.getArgsFieldBuilder() : null;
                  } else {
                     this.argsBuilder_.addAllMessages(other.args_);
                  }
               }

               if (other.hasCriteria()) {
                  this.mergeCriteria(other.getCriteria());
               }

               if (other.hasLimit()) {
                  this.mergeLimit(other.getLimit());
               }

               if (this.orderBuilder_ == null) {
                  if (!other.order_.isEmpty()) {
                     if (this.order_.isEmpty()) {
                        this.order_ = other.order_;
                        this.bitField0_ &= -65;
                     } else {
                        this.ensureOrderIsMutable();
                        this.order_.addAll(other.order_);
                     }

                     this.onChanged();
                  }
               } else if (!other.order_.isEmpty()) {
                  if (this.orderBuilder_.isEmpty()) {
                     this.orderBuilder_.dispose();
                     this.orderBuilder_ = null;
                     this.order_ = other.order_;
                     this.bitField0_ &= -65;
                     this.orderBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? this.getOrderFieldBuilder() : null;
                  } else {
                     this.orderBuilder_.addAllMessages(other.order_);
                  }
               }

               if (this.groupingBuilder_ == null) {
                  if (!other.grouping_.isEmpty()) {
                     if (this.grouping_.isEmpty()) {
                        this.grouping_ = other.grouping_;
                        this.bitField0_ &= -129;
                     } else {
                        this.ensureGroupingIsMutable();
                        this.grouping_.addAll(other.grouping_);
                     }

                     this.onChanged();
                  }
               } else if (!other.grouping_.isEmpty()) {
                  if (this.groupingBuilder_.isEmpty()) {
                     this.groupingBuilder_.dispose();
                     this.groupingBuilder_ = null;
                     this.grouping_ = other.grouping_;
                     this.bitField0_ &= -129;
                     this.groupingBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? this.getGroupingFieldBuilder() : null;
                  } else {
                     this.groupingBuilder_.addAllMessages(other.grouping_);
                  }
               }

               if (other.hasGroupingCriteria()) {
                  this.mergeGroupingCriteria(other.getGroupingCriteria());
               }

               if (other.hasLocking()) {
                  this.setLocking(other.getLocking());
               }

               if (other.hasLockingOptions()) {
                  this.setLockingOptions(other.getLockingOptions());
               }

               if (other.hasLimitExpr()) {
                  this.mergeLimitExpr(other.getLimitExpr());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else if (!this.getCollection().isInitialized()) {
               return false;
            } else {
               for (int i = 0; i < this.getProjectionCount(); i++) {
                  if (!this.getProjection(i).isInitialized()) {
                     return false;
                  }
               }

               for (int ix = 0; ix < this.getArgsCount(); ix++) {
                  if (!this.getArgs(ix).isInitialized()) {
                     return false;
                  }
               }

               if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
                  return false;
               } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
                  return false;
               } else {
                  for (int ixx = 0; ixx < this.getOrderCount(); ixx++) {
                     if (!this.getOrder(ixx).isInitialized()) {
                        return false;
                     }
                  }

                  for (int ixxx = 0; ixxx < this.getGroupingCount(); ixxx++) {
                     if (!this.getGrouping(ixxx).isInitialized()) {
                        return false;
                     }
                  }

                  return this.hasGroupingCriteria() && !this.getGroupingCriteria().isInitialized()
                     ? false
                     : !this.hasLimitExpr() || this.getLimitExpr().isInitialized();
               }
            }
         }

         public MysqlxCrud.Find.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.Find parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.Find.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.Find)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxCrud.Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return this.collectionBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Find.Builder setCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Find.Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Find.Builder mergeCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Find.Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxCrud.Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getCollectionFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3<>(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         @Override
         public boolean hasDataModel() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxCrud.DataModel getDataModel() {
            MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
            return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
         }

         public MysqlxCrud.Find.Builder setDataModel(MysqlxCrud.DataModel value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.dataModel_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Find.Builder clearDataModel() {
            this.bitField0_ &= -3;
            this.dataModel_ = 1;
            this.onChanged();
            return this;
         }

         private void ensureProjectionIsMutable() {
            if ((this.bitField0_ & 4) == 0) {
               this.projection_ = new ArrayList<>(this.projection_);
               this.bitField0_ |= 4;
            }
         }

         @Override
         public List<MysqlxCrud.Projection> getProjectionList() {
            return this.projectionBuilder_ == null ? Collections.unmodifiableList(this.projection_) : this.projectionBuilder_.getMessageList();
         }

         @Override
         public int getProjectionCount() {
            return this.projectionBuilder_ == null ? this.projection_.size() : this.projectionBuilder_.getCount();
         }

         @Override
         public MysqlxCrud.Projection getProjection(int index) {
            return this.projectionBuilder_ == null ? this.projection_.get(index) : this.projectionBuilder_.getMessage(index);
         }

         public MysqlxCrud.Find.Builder setProjection(int index, MysqlxCrud.Projection value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.set(index, value);
               this.onChanged();
            } else {
               this.projectionBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder setProjection(int index, MysqlxCrud.Projection.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addProjection(MysqlxCrud.Projection value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.add(value);
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addProjection(int index, MysqlxCrud.Projection value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.add(index, value);
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addProjection(MysqlxCrud.Projection.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addProjection(int index, MysqlxCrud.Projection.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addAllProjection(Iterable<? extends MysqlxCrud.Projection> values) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.projection_);
               this.onChanged();
            } else {
               this.projectionBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder clearProjection() {
            if (this.projectionBuilder_ == null) {
               this.projection_ = Collections.emptyList();
               this.bitField0_ &= -5;
               this.onChanged();
            } else {
               this.projectionBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Find.Builder removeProjection(int index) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.remove(index);
               this.onChanged();
            } else {
               this.projectionBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxCrud.Projection.Builder getProjectionBuilder(int index) {
            return this.getProjectionFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxCrud.ProjectionOrBuilder getProjectionOrBuilder(int index) {
            return this.projectionBuilder_ == null ? this.projection_.get(index) : this.projectionBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxCrud.ProjectionOrBuilder> getProjectionOrBuilderList() {
            return this.projectionBuilder_ != null ? this.projectionBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.projection_);
         }

         public MysqlxCrud.Projection.Builder addProjectionBuilder() {
            return this.getProjectionFieldBuilder().addBuilder(MysqlxCrud.Projection.getDefaultInstance());
         }

         public MysqlxCrud.Projection.Builder addProjectionBuilder(int index) {
            return this.getProjectionFieldBuilder().addBuilder(index, MysqlxCrud.Projection.getDefaultInstance());
         }

         public List<MysqlxCrud.Projection.Builder> getProjectionBuilderList() {
            return this.getProjectionFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxCrud.Projection, MysqlxCrud.Projection.Builder, MysqlxCrud.ProjectionOrBuilder> getProjectionFieldBuilder() {
            if (this.projectionBuilder_ == null) {
               this.projectionBuilder_ = new RepeatedFieldBuilderV3<>(this.projection_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
               this.projection_ = null;
            }

            return this.projectionBuilder_;
         }

         private void ensureArgsIsMutable() {
            if ((this.bitField0_ & 8) == 0) {
               this.args_ = new ArrayList<>(this.args_);
               this.bitField0_ |= 8;
            }
         }

         @Override
         public List<MysqlxDatatypes.Scalar> getArgsList() {
            return this.argsBuilder_ == null ? Collections.unmodifiableList(this.args_) : this.argsBuilder_.getMessageList();
         }

         @Override
         public int getArgsCount() {
            return this.argsBuilder_ == null ? this.args_.size() : this.argsBuilder_.getCount();
         }

         @Override
         public MysqlxDatatypes.Scalar getArgs(int index) {
            return this.argsBuilder_ == null ? this.args_.get(index) : this.argsBuilder_.getMessage(index);
         }

         public MysqlxCrud.Find.Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.set(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addArgs(MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.args_);
               this.onChanged();
            } else {
               this.argsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder clearArgs() {
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -9;
               this.onChanged();
            } else {
               this.argsBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Find.Builder removeArgs(int index) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.remove(index);
               this.onChanged();
            } else {
               this.argsBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
            return this.getArgsFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
            return this.argsBuilder_ == null ? this.args_.get(index) : this.argsBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
            return this.argsBuilder_ != null ? this.argsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.args_);
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
            return this.getArgsFieldBuilder().addBuilder(MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
            return this.getArgsFieldBuilder().addBuilder(index, MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
            return this.getArgsFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
            if (this.argsBuilder_ == null) {
               this.argsBuilder_ = new RepeatedFieldBuilderV3<>(this.args_, (this.bitField0_ & 8) != 0, this.getParentForChildren(), this.isClean());
               this.args_ = null;
            }

            return this.argsBuilder_;
         }

         @Override
         public boolean hasCriteria() {
            return (this.bitField0_ & 16) != 0;
         }

         @Override
         public MysqlxExpr.Expr getCriteria() {
            if (this.criteriaBuilder_ == null) {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            } else {
               return this.criteriaBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Find.Builder setCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.criteria_ = value;
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(value);
            }

            this.bitField0_ |= 16;
            return this;
         }

         public MysqlxCrud.Find.Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = builderForValue.build();
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 16;
            return this;
         }

         public MysqlxCrud.Find.Builder mergeCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial();
               } else {
                  this.criteria_ = value;
               }

               this.onChanged();
            } else {
               this.criteriaBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 16;
            return this;
         }

         public MysqlxCrud.Find.Builder clearCriteria() {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
               this.onChanged();
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -17;
            return this;
         }

         public MysqlxExpr.Expr.Builder getCriteriaBuilder() {
            this.bitField0_ |= 16;
            this.onChanged();
            return this.getCriteriaFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
            if (this.criteriaBuilder_ != null) {
               return this.criteriaBuilder_.getMessageOrBuilder();
            } else {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() {
            if (this.criteriaBuilder_ == null) {
               this.criteriaBuilder_ = new SingleFieldBuilderV3<>(this.getCriteria(), this.getParentForChildren(), this.isClean());
               this.criteria_ = null;
            }

            return this.criteriaBuilder_;
         }

         @Override
         public boolean hasLimit() {
            return (this.bitField0_ & 32) != 0;
         }

         @Override
         public MysqlxCrud.Limit getLimit() {
            if (this.limitBuilder_ == null) {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            } else {
               return this.limitBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Find.Builder setLimit(MysqlxCrud.Limit value) {
            if (this.limitBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limit_ = value;
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(value);
            }

            this.bitField0_ |= 32;
            return this;
         }

         public MysqlxCrud.Find.Builder setLimit(MysqlxCrud.Limit.Builder builderForValue) {
            if (this.limitBuilder_ == null) {
               this.limit_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 32;
            return this;
         }

         public MysqlxCrud.Find.Builder mergeLimit(MysqlxCrud.Limit value) {
            if (this.limitBuilder_ == null) {
               if ((this.bitField0_ & 32) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) {
                  this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial();
               } else {
                  this.limit_ = value;
               }

               this.onChanged();
            } else {
               this.limitBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 32;
            return this;
         }

         public MysqlxCrud.Find.Builder clearLimit() {
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
               this.onChanged();
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -33;
            return this;
         }

         public MysqlxCrud.Limit.Builder getLimitBuilder() {
            this.bitField0_ |= 32;
            this.onChanged();
            return this.getLimitFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
            if (this.limitBuilder_ != null) {
               return this.limitBuilder_.getMessageOrBuilder();
            } else {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> getLimitFieldBuilder() {
            if (this.limitBuilder_ == null) {
               this.limitBuilder_ = new SingleFieldBuilderV3<>(this.getLimit(), this.getParentForChildren(), this.isClean());
               this.limit_ = null;
            }

            return this.limitBuilder_;
         }

         private void ensureOrderIsMutable() {
            if ((this.bitField0_ & 64) == 0) {
               this.order_ = new ArrayList<>(this.order_);
               this.bitField0_ |= 64;
            }
         }

         @Override
         public List<MysqlxCrud.Order> getOrderList() {
            return this.orderBuilder_ == null ? Collections.unmodifiableList(this.order_) : this.orderBuilder_.getMessageList();
         }

         @Override
         public int getOrderCount() {
            return this.orderBuilder_ == null ? this.order_.size() : this.orderBuilder_.getCount();
         }

         @Override
         public MysqlxCrud.Order getOrder(int index) {
            return this.orderBuilder_ == null ? this.order_.get(index) : this.orderBuilder_.getMessage(index);
         }

         public MysqlxCrud.Find.Builder setOrder(int index, MysqlxCrud.Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.set(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder setOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addOrder(MysqlxCrud.Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addOrder(int index, MysqlxCrud.Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addOrder(MysqlxCrud.Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addAllOrder(Iterable<? extends MysqlxCrud.Order> values) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.order_);
               this.onChanged();
            } else {
               this.orderBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder clearOrder() {
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -65;
               this.onChanged();
            } else {
               this.orderBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Find.Builder removeOrder(int index) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.remove(index);
               this.onChanged();
            } else {
               this.orderBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxCrud.Order.Builder getOrderBuilder(int index) {
            return this.getOrderFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
            return this.orderBuilder_ == null ? this.order_.get(index) : this.orderBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
            return this.orderBuilder_ != null ? this.orderBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.order_);
         }

         public MysqlxCrud.Order.Builder addOrderBuilder() {
            return this.getOrderFieldBuilder().addBuilder(MysqlxCrud.Order.getDefaultInstance());
         }

         public MysqlxCrud.Order.Builder addOrderBuilder(int index) {
            return this.getOrderFieldBuilder().addBuilder(index, MysqlxCrud.Order.getDefaultInstance());
         }

         public List<MysqlxCrud.Order.Builder> getOrderBuilderList() {
            return this.getOrderFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> getOrderFieldBuilder() {
            if (this.orderBuilder_ == null) {
               this.orderBuilder_ = new RepeatedFieldBuilderV3<>(this.order_, (this.bitField0_ & 64) != 0, this.getParentForChildren(), this.isClean());
               this.order_ = null;
            }

            return this.orderBuilder_;
         }

         private void ensureGroupingIsMutable() {
            if ((this.bitField0_ & 128) == 0) {
               this.grouping_ = new ArrayList<>(this.grouping_);
               this.bitField0_ |= 128;
            }
         }

         @Override
         public List<MysqlxExpr.Expr> getGroupingList() {
            return this.groupingBuilder_ == null ? Collections.unmodifiableList(this.grouping_) : this.groupingBuilder_.getMessageList();
         }

         @Override
         public int getGroupingCount() {
            return this.groupingBuilder_ == null ? this.grouping_.size() : this.groupingBuilder_.getCount();
         }

         @Override
         public MysqlxExpr.Expr getGrouping(int index) {
            return this.groupingBuilder_ == null ? this.grouping_.get(index) : this.groupingBuilder_.getMessage(index);
         }

         public MysqlxCrud.Find.Builder setGrouping(int index, MysqlxExpr.Expr value) {
            if (this.groupingBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureGroupingIsMutable();
               this.grouping_.set(index, value);
               this.onChanged();
            } else {
               this.groupingBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder setGrouping(int index, MysqlxExpr.Expr.Builder builderForValue) {
            if (this.groupingBuilder_ == null) {
               this.ensureGroupingIsMutable();
               this.grouping_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.groupingBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addGrouping(MysqlxExpr.Expr value) {
            if (this.groupingBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureGroupingIsMutable();
               this.grouping_.add(value);
               this.onChanged();
            } else {
               this.groupingBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addGrouping(int index, MysqlxExpr.Expr value) {
            if (this.groupingBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureGroupingIsMutable();
               this.grouping_.add(index, value);
               this.onChanged();
            } else {
               this.groupingBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addGrouping(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.groupingBuilder_ == null) {
               this.ensureGroupingIsMutable();
               this.grouping_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.groupingBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addGrouping(int index, MysqlxExpr.Expr.Builder builderForValue) {
            if (this.groupingBuilder_ == null) {
               this.ensureGroupingIsMutable();
               this.grouping_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.groupingBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Find.Builder addAllGrouping(Iterable<? extends MysqlxExpr.Expr> values) {
            if (this.groupingBuilder_ == null) {
               this.ensureGroupingIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.grouping_);
               this.onChanged();
            } else {
               this.groupingBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Find.Builder clearGrouping() {
            if (this.groupingBuilder_ == null) {
               this.grouping_ = Collections.emptyList();
               this.bitField0_ &= -129;
               this.onChanged();
            } else {
               this.groupingBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Find.Builder removeGrouping(int index) {
            if (this.groupingBuilder_ == null) {
               this.ensureGroupingIsMutable();
               this.grouping_.remove(index);
               this.onChanged();
            } else {
               this.groupingBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxExpr.Expr.Builder getGroupingBuilder(int index) {
            return this.getGroupingFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int index) {
            return this.groupingBuilder_ == null ? this.grouping_.get(index) : this.groupingBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList() {
            return this.groupingBuilder_ != null ? this.groupingBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.grouping_);
         }

         public MysqlxExpr.Expr.Builder addGroupingBuilder() {
            return this.getGroupingFieldBuilder().addBuilder(MysqlxExpr.Expr.getDefaultInstance());
         }

         public MysqlxExpr.Expr.Builder addGroupingBuilder(int index) {
            return this.getGroupingFieldBuilder().addBuilder(index, MysqlxExpr.Expr.getDefaultInstance());
         }

         public List<MysqlxExpr.Expr.Builder> getGroupingBuilderList() {
            return this.getGroupingFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getGroupingFieldBuilder() {
            if (this.groupingBuilder_ == null) {
               this.groupingBuilder_ = new RepeatedFieldBuilderV3<>(this.grouping_, (this.bitField0_ & 128) != 0, this.getParentForChildren(), this.isClean());
               this.grouping_ = null;
            }

            return this.groupingBuilder_;
         }

         @Override
         public boolean hasGroupingCriteria() {
            return (this.bitField0_ & 256) != 0;
         }

         @Override
         public MysqlxExpr.Expr getGroupingCriteria() {
            if (this.groupingCriteriaBuilder_ == null) {
               return this.groupingCriteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;
            } else {
               return this.groupingCriteriaBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Find.Builder setGroupingCriteria(MysqlxExpr.Expr value) {
            if (this.groupingCriteriaBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.groupingCriteria_ = value;
               this.onChanged();
            } else {
               this.groupingCriteriaBuilder_.setMessage(value);
            }

            this.bitField0_ |= 256;
            return this;
         }

         public MysqlxCrud.Find.Builder setGroupingCriteria(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.groupingCriteriaBuilder_ == null) {
               this.groupingCriteria_ = builderForValue.build();
               this.onChanged();
            } else {
               this.groupingCriteriaBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 256;
            return this;
         }

         public MysqlxCrud.Find.Builder mergeGroupingCriteria(MysqlxExpr.Expr value) {
            if (this.groupingCriteriaBuilder_ == null) {
               if ((this.bitField0_ & 256) != 0 && this.groupingCriteria_ != null && this.groupingCriteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.groupingCriteria_ = MysqlxExpr.Expr.newBuilder(this.groupingCriteria_).mergeFrom(value).buildPartial();
               } else {
                  this.groupingCriteria_ = value;
               }

               this.onChanged();
            } else {
               this.groupingCriteriaBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 256;
            return this;
         }

         public MysqlxCrud.Find.Builder clearGroupingCriteria() {
            if (this.groupingCriteriaBuilder_ == null) {
               this.groupingCriteria_ = null;
               this.onChanged();
            } else {
               this.groupingCriteriaBuilder_.clear();
            }

            this.bitField0_ &= -257;
            return this;
         }

         public MysqlxExpr.Expr.Builder getGroupingCriteriaBuilder() {
            this.bitField0_ |= 256;
            this.onChanged();
            return this.getGroupingCriteriaFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder() {
            if (this.groupingCriteriaBuilder_ != null) {
               return this.groupingCriteriaBuilder_.getMessageOrBuilder();
            } else {
               return this.groupingCriteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getGroupingCriteriaFieldBuilder() {
            if (this.groupingCriteriaBuilder_ == null) {
               this.groupingCriteriaBuilder_ = new SingleFieldBuilderV3<>(this.getGroupingCriteria(), this.getParentForChildren(), this.isClean());
               this.groupingCriteria_ = null;
            }

            return this.groupingCriteriaBuilder_;
         }

         @Override
         public boolean hasLocking() {
            return (this.bitField0_ & 512) != 0;
         }

         @Override
         public MysqlxCrud.Find.RowLock getLocking() {
            MysqlxCrud.Find.RowLock result = MysqlxCrud.Find.RowLock.valueOf(this.locking_);
            return result == null ? MysqlxCrud.Find.RowLock.SHARED_LOCK : result;
         }

         public MysqlxCrud.Find.Builder setLocking(MysqlxCrud.Find.RowLock value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 512;
               this.locking_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Find.Builder clearLocking() {
            this.bitField0_ &= -513;
            this.locking_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasLockingOptions() {
            return (this.bitField0_ & 1024) != 0;
         }

         @Override
         public MysqlxCrud.Find.RowLockOptions getLockingOptions() {
            MysqlxCrud.Find.RowLockOptions result = MysqlxCrud.Find.RowLockOptions.valueOf(this.lockingOptions_);
            return result == null ? MysqlxCrud.Find.RowLockOptions.NOWAIT : result;
         }

         public MysqlxCrud.Find.Builder setLockingOptions(MysqlxCrud.Find.RowLockOptions value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1024;
               this.lockingOptions_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Find.Builder clearLockingOptions() {
            this.bitField0_ &= -1025;
            this.lockingOptions_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasLimitExpr() {
            return (this.bitField0_ & 2048) != 0;
         }

         @Override
         public MysqlxCrud.LimitExpr getLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            } else {
               return this.limitExprBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Find.Builder setLimitExpr(MysqlxCrud.LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limitExpr_ = value;
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(value);
            }

            this.bitField0_ |= 2048;
            return this;
         }

         public MysqlxCrud.Find.Builder setLimitExpr(MysqlxCrud.LimitExpr.Builder builderForValue) {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2048;
            return this;
         }

         public MysqlxCrud.Find.Builder mergeLimitExpr(MysqlxCrud.LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if ((this.bitField0_ & 2048) != 0 && this.limitExpr_ != null && this.limitExpr_ != MysqlxCrud.LimitExpr.getDefaultInstance()) {
                  this.limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
               } else {
                  this.limitExpr_ = value;
               }

               this.onChanged();
            } else {
               this.limitExprBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 2048;
            return this;
         }

         public MysqlxCrud.Find.Builder clearLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
               this.onChanged();
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -2049;
            return this;
         }

         public MysqlxCrud.LimitExpr.Builder getLimitExprBuilder() {
            this.bitField0_ |= 2048;
            this.onChanged();
            return this.getLimitExprFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
            if (this.limitExprBuilder_ != null) {
               return this.limitExprBuilder_.getMessageOrBuilder();
            } else {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> getLimitExprFieldBuilder() {
            if (this.limitExprBuilder_ == null) {
               this.limitExprBuilder_ = new SingleFieldBuilderV3<>(this.getLimitExpr(), this.getParentForChildren(), this.isClean());
               this.limitExpr_ = null;
            }

            return this.limitExprBuilder_;
         }

         public final MysqlxCrud.Find.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Find.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.Find.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Find.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static enum RowLock implements ProtocolMessageEnum {
         SHARED_LOCK(1),
         EXCLUSIVE_LOCK(2);

         public static final int SHARED_LOCK_VALUE = 1;
         public static final int EXCLUSIVE_LOCK_VALUE = 2;
         private static final Internal.EnumLiteMap<MysqlxCrud.Find.RowLock> internalValueMap = new Internal.EnumLiteMap<MysqlxCrud.Find.RowLock>() {
            public MysqlxCrud.Find.RowLock findValueByNumber(int number) {
               return MysqlxCrud.Find.RowLock.forNumber(number);
            }
         };
         private static final MysqlxCrud.Find.RowLock[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxCrud.Find.RowLock valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxCrud.Find.RowLock forNumber(int value) {
            switch (value) {
               case 1:
                  return SHARED_LOCK;
               case 2:
                  return EXCLUSIVE_LOCK;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<MysqlxCrud.Find.RowLock> internalGetValueMap() {
            return internalValueMap;
         }

         @Override
         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return getDescriptor().getValues().get(this.ordinal());
         }

         @Override
         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return MysqlxCrud.Find.getDescriptor().getEnumTypes().get(0);
         }

         public static MysqlxCrud.Find.RowLock valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private RowLock(int value) {
            this.value = value;
         }
      }

      public static enum RowLockOptions implements ProtocolMessageEnum {
         NOWAIT(1),
         SKIP_LOCKED(2);

         public static final int NOWAIT_VALUE = 1;
         public static final int SKIP_LOCKED_VALUE = 2;
         private static final Internal.EnumLiteMap<MysqlxCrud.Find.RowLockOptions> internalValueMap = new Internal.EnumLiteMap<MysqlxCrud.Find.RowLockOptions>() {
            public MysqlxCrud.Find.RowLockOptions findValueByNumber(int number) {
               return MysqlxCrud.Find.RowLockOptions.forNumber(number);
            }
         };
         private static final MysqlxCrud.Find.RowLockOptions[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxCrud.Find.RowLockOptions valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxCrud.Find.RowLockOptions forNumber(int value) {
            switch (value) {
               case 1:
                  return NOWAIT;
               case 2:
                  return SKIP_LOCKED;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<MysqlxCrud.Find.RowLockOptions> internalGetValueMap() {
            return internalValueMap;
         }

         @Override
         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return getDescriptor().getValues().get(this.ordinal());
         }

         @Override
         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return MysqlxCrud.Find.getDescriptor().getEnumTypes().get(1);
         }

         public static MysqlxCrud.Find.RowLockOptions valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private RowLockOptions(int value) {
            this.value = value;
         }
      }
   }

   public interface FindOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      MysqlxCrud.Collection getCollection();

      MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDataModel();

      MysqlxCrud.DataModel getDataModel();

      List<MysqlxCrud.Projection> getProjectionList();

      MysqlxCrud.Projection getProjection(int var1);

      int getProjectionCount();

      List<? extends MysqlxCrud.ProjectionOrBuilder> getProjectionOrBuilderList();

      MysqlxCrud.ProjectionOrBuilder getProjectionOrBuilder(int var1);

      List<MysqlxDatatypes.Scalar> getArgsList();

      MysqlxDatatypes.Scalar getArgs(int var1);

      int getArgsCount();

      List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();

      MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int var1);

      boolean hasCriteria();

      MysqlxExpr.Expr getCriteria();

      MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();

      boolean hasLimit();

      MysqlxCrud.Limit getLimit();

      MysqlxCrud.LimitOrBuilder getLimitOrBuilder();

      List<MysqlxCrud.Order> getOrderList();

      MysqlxCrud.Order getOrder(int var1);

      int getOrderCount();

      List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList();

      MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int var1);

      List<MysqlxExpr.Expr> getGroupingList();

      MysqlxExpr.Expr getGrouping(int var1);

      int getGroupingCount();

      List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList();

      MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int var1);

      boolean hasGroupingCriteria();

      MysqlxExpr.Expr getGroupingCriteria();

      MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder();

      boolean hasLocking();

      MysqlxCrud.Find.RowLock getLocking();

      boolean hasLockingOptions();

      MysqlxCrud.Find.RowLockOptions getLockingOptions();

      boolean hasLimitExpr();

      MysqlxCrud.LimitExpr getLimitExpr();

      MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder();
   }

   public static final class Insert extends GeneratedMessageV3 implements MysqlxCrud.InsertOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 1;
      private MysqlxCrud.Collection collection_;
      public static final int DATA_MODEL_FIELD_NUMBER = 2;
      private int dataModel_;
      public static final int PROJECTION_FIELD_NUMBER = 3;
      private List<MysqlxCrud.Column> projection_;
      public static final int ROW_FIELD_NUMBER = 4;
      private List<MysqlxCrud.Insert.TypedRow> row_;
      public static final int ARGS_FIELD_NUMBER = 5;
      private List<MysqlxDatatypes.Scalar> args_;
      public static final int UPSERT_FIELD_NUMBER = 6;
      private boolean upsert_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.Insert DEFAULT_INSTANCE = new MysqlxCrud.Insert();
      @Deprecated
      public static final Parser<MysqlxCrud.Insert> PARSER = new AbstractParser<MysqlxCrud.Insert>() {
         public MysqlxCrud.Insert parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.Insert(input, extensionRegistry);
         }
      };

      private Insert(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Insert() {
         this.dataModel_ = 1;
         this.projection_ = Collections.emptyList();
         this.row_ = Collections.emptyList();
         this.args_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.Insert();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Insert(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxCrud.Collection.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.collection_.toBuilder();
                        }

                        this.collection_ = input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.collection_);
                           this.collection_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        int rawValue = input.readEnum();
                        MysqlxCrud.DataModel value = MysqlxCrud.DataModel.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(2, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.dataModel_ = rawValue;
                        }
                        break;
                     case 26:
                        if ((mutable_bitField0_ & 4) == 0) {
                           this.projection_ = new ArrayList<>();
                           mutable_bitField0_ |= 4;
                        }

                        this.projection_.add(input.readMessage(MysqlxCrud.Column.PARSER, extensionRegistry));
                        break;
                     case 34:
                        if ((mutable_bitField0_ & 8) == 0) {
                           this.row_ = new ArrayList<>();
                           mutable_bitField0_ |= 8;
                        }

                        this.row_.add(input.readMessage(MysqlxCrud.Insert.TypedRow.PARSER, extensionRegistry));
                        break;
                     case 42:
                        if ((mutable_bitField0_ & 16) == 0) {
                           this.args_ = new ArrayList<>();
                           mutable_bitField0_ |= 16;
                        }

                        this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry));
                        break;
                     case 48:
                        this.bitField0_ |= 4;
                        this.upsert_ = input.readBool();
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw new InvalidProtocolBufferException(var14).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 4) != 0) {
                  this.projection_ = Collections.unmodifiableList(this.projection_);
               }

               if ((mutable_bitField0_ & 8) != 0) {
                  this.row_ = Collections.unmodifiableList(this.row_);
               }

               if ((mutable_bitField0_ & 16) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.Insert.class, MysqlxCrud.Insert.Builder.class);
      }

      @Override
      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxCrud.Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public boolean hasDataModel() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public MysqlxCrud.DataModel getDataModel() {
         MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
         return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
      }

      @Override
      public List<MysqlxCrud.Column> getProjectionList() {
         return this.projection_;
      }

      @Override
      public List<? extends MysqlxCrud.ColumnOrBuilder> getProjectionOrBuilderList() {
         return this.projection_;
      }

      @Override
      public int getProjectionCount() {
         return this.projection_.size();
      }

      @Override
      public MysqlxCrud.Column getProjection(int index) {
         return this.projection_.get(index);
      }

      @Override
      public MysqlxCrud.ColumnOrBuilder getProjectionOrBuilder(int index) {
         return this.projection_.get(index);
      }

      @Override
      public List<MysqlxCrud.Insert.TypedRow> getRowList() {
         return this.row_;
      }

      @Override
      public List<? extends MysqlxCrud.Insert.TypedRowOrBuilder> getRowOrBuilderList() {
         return this.row_;
      }

      @Override
      public int getRowCount() {
         return this.row_.size();
      }

      @Override
      public MysqlxCrud.Insert.TypedRow getRow(int index) {
         return this.row_.get(index);
      }

      @Override
      public MysqlxCrud.Insert.TypedRowOrBuilder getRowOrBuilder(int index) {
         return this.row_.get(index);
      }

      @Override
      public List<MysqlxDatatypes.Scalar> getArgsList() {
         return this.args_;
      }

      @Override
      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
         return this.args_;
      }

      @Override
      public int getArgsCount() {
         return this.args_.size();
      }

      @Override
      public MysqlxDatatypes.Scalar getArgs(int index) {
         return this.args_.get(index);
      }

      @Override
      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
         return this.args_.get(index);
      }

      @Override
      public boolean hasUpsert() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public boolean getUpsert() {
         return this.upsert_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            for (int i = 0; i < this.getProjectionCount(); i++) {
               if (!this.getProjection(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for (int ix = 0; ix < this.getRowCount(); ix++) {
               if (!this.getRow(ix).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for (int ixx = 0; ixx < this.getArgsCount(); ixx++) {
               if (!this.getArgs(ixx).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.dataModel_);
         }

         for (int i = 0; i < this.projection_.size(); i++) {
            output.writeMessage(3, this.projection_.get(i));
         }

         for (int i = 0; i < this.row_.size(); i++) {
            output.writeMessage(4, this.row_.get(i));
         }

         for (int i = 0; i < this.args_.size(); i++) {
            output.writeMessage(5, this.args_.get(i));
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeBool(6, this.upsert_);
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.dataModel_);
            }

            for (int i = 0; i < this.projection_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(3, this.projection_.get(i));
            }

            for (int i = 0; i < this.row_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(4, this.row_.get(i));
            }

            for (int i = 0; i < this.args_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(5, this.args_.get(i));
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeBoolSize(6, this.upsert_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.Insert)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.Insert other = (MysqlxCrud.Insert)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDataModel() != other.hasDataModel()) {
               return false;
            } else if (this.hasDataModel() && this.dataModel_ != other.dataModel_) {
               return false;
            } else if (!this.getProjectionList().equals(other.getProjectionList())) {
               return false;
            } else if (!this.getRowList().equals(other.getRowList())) {
               return false;
            } else if (!this.getArgsList().equals(other.getArgsList())) {
               return false;
            } else if (this.hasUpsert() != other.hasUpsert()) {
               return false;
            } else {
               return this.hasUpsert() && this.getUpsert() != other.getUpsert() ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDataModel()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.dataModel_;
            }

            if (this.getProjectionCount() > 0) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getProjectionList().hashCode();
            }

            if (this.getRowCount() > 0) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getRowList().hashCode();
            }

            if (this.getArgsCount() > 0) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getArgsList().hashCode();
            }

            if (this.hasUpsert()) {
               hash = 37 * hash + 6;
               hash = 53 * hash + Internal.hashBoolean(this.getUpsert());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.Insert parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Insert parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Insert parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Insert parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Insert parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Insert parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Insert parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Insert parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Insert parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Insert parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Insert parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Insert parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.Insert.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.Insert.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.Insert.Builder newBuilder(MysqlxCrud.Insert prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.Insert.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.Insert.Builder() : new MysqlxCrud.Insert.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.Insert.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.Insert.Builder(parent);
      }

      public static MysqlxCrud.Insert getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.Insert> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.Insert> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.Insert getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.Insert.Builder> implements MysqlxCrud.InsertOrBuilder {
         private int bitField0_;
         private MysqlxCrud.Collection collection_;
         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
         private int dataModel_ = 1;
         private List<MysqlxCrud.Column> projection_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxCrud.Column, MysqlxCrud.Column.Builder, MysqlxCrud.ColumnOrBuilder> projectionBuilder_;
         private List<MysqlxCrud.Insert.TypedRow> row_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxCrud.Insert.TypedRow, MysqlxCrud.Insert.TypedRow.Builder, MysqlxCrud.Insert.TypedRowOrBuilder> rowBuilder_;
         private List<MysqlxDatatypes.Scalar> args_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
         private boolean upsert_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.Insert.class, MysqlxCrud.Insert.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Insert.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getProjectionFieldBuilder();
               this.getRowFieldBuilder();
               this.getArgsFieldBuilder();
            }
         }

         public MysqlxCrud.Insert.Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.dataModel_ = 1;
            this.bitField0_ &= -3;
            if (this.projectionBuilder_ == null) {
               this.projection_ = Collections.emptyList();
               this.bitField0_ &= -5;
            } else {
               this.projectionBuilder_.clear();
            }

            if (this.rowBuilder_ == null) {
               this.row_ = Collections.emptyList();
               this.bitField0_ &= -9;
            } else {
               this.rowBuilder_.clear();
            }

            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -17;
            } else {
               this.argsBuilder_.clear();
            }

            this.upsert_ = false;
            this.bitField0_ &= -33;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor;
         }

         public MysqlxCrud.Insert getDefaultInstanceForType() {
            return MysqlxCrud.Insert.getDefaultInstance();
         }

         public MysqlxCrud.Insert build() {
            MysqlxCrud.Insert result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.Insert buildPartial() {
            MysqlxCrud.Insert result = new MysqlxCrud.Insert(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.dataModel_ = this.dataModel_;
            if (this.projectionBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0) {
                  this.projection_ = Collections.unmodifiableList(this.projection_);
                  this.bitField0_ &= -5;
               }

               result.projection_ = this.projection_;
            } else {
               result.projection_ = this.projectionBuilder_.build();
            }

            if (this.rowBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0) {
                  this.row_ = Collections.unmodifiableList(this.row_);
                  this.bitField0_ &= -9;
               }

               result.row_ = this.row_;
            } else {
               result.row_ = this.rowBuilder_.build();
            }

            if (this.argsBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
                  this.bitField0_ &= -17;
               }

               result.args_ = this.args_;
            } else {
               result.args_ = this.argsBuilder_.build();
            }

            if ((from_bitField0_ & 32) != 0) {
               result.upsert_ = this.upsert_;
               to_bitField0_ |= 4;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.Insert.Builder clone() {
            return (MysqlxCrud.Insert.Builder)super.clone();
         }

         public MysqlxCrud.Insert.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Insert.Builder)super.setField(field, value);
         }

         public MysqlxCrud.Insert.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.Insert.Builder)super.clearField(field);
         }

         public MysqlxCrud.Insert.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.Insert.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.Insert.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.Insert.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.Insert.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Insert.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.Insert.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.Insert) {
               return this.mergeFrom((MysqlxCrud.Insert)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.Insert.Builder mergeFrom(MysqlxCrud.Insert other) {
            if (other == MysqlxCrud.Insert.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDataModel()) {
                  this.setDataModel(other.getDataModel());
               }

               if (this.projectionBuilder_ == null) {
                  if (!other.projection_.isEmpty()) {
                     if (this.projection_.isEmpty()) {
                        this.projection_ = other.projection_;
                        this.bitField0_ &= -5;
                     } else {
                        this.ensureProjectionIsMutable();
                        this.projection_.addAll(other.projection_);
                     }

                     this.onChanged();
                  }
               } else if (!other.projection_.isEmpty()) {
                  if (this.projectionBuilder_.isEmpty()) {
                     this.projectionBuilder_.dispose();
                     this.projectionBuilder_ = null;
                     this.projection_ = other.projection_;
                     this.bitField0_ &= -5;
                     this.projectionBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? this.getProjectionFieldBuilder() : null;
                  } else {
                     this.projectionBuilder_.addAllMessages(other.projection_);
                  }
               }

               if (this.rowBuilder_ == null) {
                  if (!other.row_.isEmpty()) {
                     if (this.row_.isEmpty()) {
                        this.row_ = other.row_;
                        this.bitField0_ &= -9;
                     } else {
                        this.ensureRowIsMutable();
                        this.row_.addAll(other.row_);
                     }

                     this.onChanged();
                  }
               } else if (!other.row_.isEmpty()) {
                  if (this.rowBuilder_.isEmpty()) {
                     this.rowBuilder_.dispose();
                     this.rowBuilder_ = null;
                     this.row_ = other.row_;
                     this.bitField0_ &= -9;
                     this.rowBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? this.getRowFieldBuilder() : null;
                  } else {
                     this.rowBuilder_.addAllMessages(other.row_);
                  }
               }

               if (this.argsBuilder_ == null) {
                  if (!other.args_.isEmpty()) {
                     if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= -17;
                     } else {
                        this.ensureArgsIsMutable();
                        this.args_.addAll(other.args_);
                     }

                     this.onChanged();
                  }
               } else if (!other.args_.isEmpty()) {
                  if (this.argsBuilder_.isEmpty()) {
                     this.argsBuilder_.dispose();
                     this.argsBuilder_ = null;
                     this.args_ = other.args_;
                     this.bitField0_ &= -17;
                     this.argsBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? this.getArgsFieldBuilder() : null;
                  } else {
                     this.argsBuilder_.addAllMessages(other.args_);
                  }
               }

               if (other.hasUpsert()) {
                  this.setUpsert(other.getUpsert());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else if (!this.getCollection().isInitialized()) {
               return false;
            } else {
               for (int i = 0; i < this.getProjectionCount(); i++) {
                  if (!this.getProjection(i).isInitialized()) {
                     return false;
                  }
               }

               for (int ix = 0; ix < this.getRowCount(); ix++) {
                  if (!this.getRow(ix).isInitialized()) {
                     return false;
                  }
               }

               for (int ixx = 0; ixx < this.getArgsCount(); ixx++) {
                  if (!this.getArgs(ixx).isInitialized()) {
                     return false;
                  }
               }

               return true;
            }
         }

         public MysqlxCrud.Insert.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.Insert parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.Insert.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.Insert)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxCrud.Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return this.collectionBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Insert.Builder setCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Insert.Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Insert.Builder mergeCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Insert.Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxCrud.Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getCollectionFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3<>(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         @Override
         public boolean hasDataModel() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxCrud.DataModel getDataModel() {
            MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
            return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
         }

         public MysqlxCrud.Insert.Builder setDataModel(MysqlxCrud.DataModel value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.dataModel_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Insert.Builder clearDataModel() {
            this.bitField0_ &= -3;
            this.dataModel_ = 1;
            this.onChanged();
            return this;
         }

         private void ensureProjectionIsMutable() {
            if ((this.bitField0_ & 4) == 0) {
               this.projection_ = new ArrayList<>(this.projection_);
               this.bitField0_ |= 4;
            }
         }

         @Override
         public List<MysqlxCrud.Column> getProjectionList() {
            return this.projectionBuilder_ == null ? Collections.unmodifiableList(this.projection_) : this.projectionBuilder_.getMessageList();
         }

         @Override
         public int getProjectionCount() {
            return this.projectionBuilder_ == null ? this.projection_.size() : this.projectionBuilder_.getCount();
         }

         @Override
         public MysqlxCrud.Column getProjection(int index) {
            return this.projectionBuilder_ == null ? this.projection_.get(index) : this.projectionBuilder_.getMessage(index);
         }

         public MysqlxCrud.Insert.Builder setProjection(int index, MysqlxCrud.Column value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.set(index, value);
               this.onChanged();
            } else {
               this.projectionBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder setProjection(int index, MysqlxCrud.Column.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addProjection(MysqlxCrud.Column value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.add(value);
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addProjection(int index, MysqlxCrud.Column value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.add(index, value);
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addProjection(MysqlxCrud.Column.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addProjection(int index, MysqlxCrud.Column.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addAllProjection(Iterable<? extends MysqlxCrud.Column> values) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.projection_);
               this.onChanged();
            } else {
               this.projectionBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder clearProjection() {
            if (this.projectionBuilder_ == null) {
               this.projection_ = Collections.emptyList();
               this.bitField0_ &= -5;
               this.onChanged();
            } else {
               this.projectionBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder removeProjection(int index) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.remove(index);
               this.onChanged();
            } else {
               this.projectionBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxCrud.Column.Builder getProjectionBuilder(int index) {
            return this.getProjectionFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxCrud.ColumnOrBuilder getProjectionOrBuilder(int index) {
            return this.projectionBuilder_ == null ? this.projection_.get(index) : this.projectionBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxCrud.ColumnOrBuilder> getProjectionOrBuilderList() {
            return this.projectionBuilder_ != null ? this.projectionBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.projection_);
         }

         public MysqlxCrud.Column.Builder addProjectionBuilder() {
            return this.getProjectionFieldBuilder().addBuilder(MysqlxCrud.Column.getDefaultInstance());
         }

         public MysqlxCrud.Column.Builder addProjectionBuilder(int index) {
            return this.getProjectionFieldBuilder().addBuilder(index, MysqlxCrud.Column.getDefaultInstance());
         }

         public List<MysqlxCrud.Column.Builder> getProjectionBuilderList() {
            return this.getProjectionFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxCrud.Column, MysqlxCrud.Column.Builder, MysqlxCrud.ColumnOrBuilder> getProjectionFieldBuilder() {
            if (this.projectionBuilder_ == null) {
               this.projectionBuilder_ = new RepeatedFieldBuilderV3<>(this.projection_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
               this.projection_ = null;
            }

            return this.projectionBuilder_;
         }

         private void ensureRowIsMutable() {
            if ((this.bitField0_ & 8) == 0) {
               this.row_ = new ArrayList<>(this.row_);
               this.bitField0_ |= 8;
            }
         }

         @Override
         public List<MysqlxCrud.Insert.TypedRow> getRowList() {
            return this.rowBuilder_ == null ? Collections.unmodifiableList(this.row_) : this.rowBuilder_.getMessageList();
         }

         @Override
         public int getRowCount() {
            return this.rowBuilder_ == null ? this.row_.size() : this.rowBuilder_.getCount();
         }

         @Override
         public MysqlxCrud.Insert.TypedRow getRow(int index) {
            return this.rowBuilder_ == null ? this.row_.get(index) : this.rowBuilder_.getMessage(index);
         }

         public MysqlxCrud.Insert.Builder setRow(int index, MysqlxCrud.Insert.TypedRow value) {
            if (this.rowBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureRowIsMutable();
               this.row_.set(index, value);
               this.onChanged();
            } else {
               this.rowBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder setRow(int index, MysqlxCrud.Insert.TypedRow.Builder builderForValue) {
            if (this.rowBuilder_ == null) {
               this.ensureRowIsMutable();
               this.row_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.rowBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addRow(MysqlxCrud.Insert.TypedRow value) {
            if (this.rowBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureRowIsMutable();
               this.row_.add(value);
               this.onChanged();
            } else {
               this.rowBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addRow(int index, MysqlxCrud.Insert.TypedRow value) {
            if (this.rowBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureRowIsMutable();
               this.row_.add(index, value);
               this.onChanged();
            } else {
               this.rowBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addRow(MysqlxCrud.Insert.TypedRow.Builder builderForValue) {
            if (this.rowBuilder_ == null) {
               this.ensureRowIsMutable();
               this.row_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.rowBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addRow(int index, MysqlxCrud.Insert.TypedRow.Builder builderForValue) {
            if (this.rowBuilder_ == null) {
               this.ensureRowIsMutable();
               this.row_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.rowBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addAllRow(Iterable<? extends MysqlxCrud.Insert.TypedRow> values) {
            if (this.rowBuilder_ == null) {
               this.ensureRowIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.row_);
               this.onChanged();
            } else {
               this.rowBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder clearRow() {
            if (this.rowBuilder_ == null) {
               this.row_ = Collections.emptyList();
               this.bitField0_ &= -9;
               this.onChanged();
            } else {
               this.rowBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder removeRow(int index) {
            if (this.rowBuilder_ == null) {
               this.ensureRowIsMutable();
               this.row_.remove(index);
               this.onChanged();
            } else {
               this.rowBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxCrud.Insert.TypedRow.Builder getRowBuilder(int index) {
            return this.getRowFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxCrud.Insert.TypedRowOrBuilder getRowOrBuilder(int index) {
            return this.rowBuilder_ == null ? this.row_.get(index) : this.rowBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxCrud.Insert.TypedRowOrBuilder> getRowOrBuilderList() {
            return this.rowBuilder_ != null ? this.rowBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.row_);
         }

         public MysqlxCrud.Insert.TypedRow.Builder addRowBuilder() {
            return this.getRowFieldBuilder().addBuilder(MysqlxCrud.Insert.TypedRow.getDefaultInstance());
         }

         public MysqlxCrud.Insert.TypedRow.Builder addRowBuilder(int index) {
            return this.getRowFieldBuilder().addBuilder(index, MysqlxCrud.Insert.TypedRow.getDefaultInstance());
         }

         public List<MysqlxCrud.Insert.TypedRow.Builder> getRowBuilderList() {
            return this.getRowFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxCrud.Insert.TypedRow, MysqlxCrud.Insert.TypedRow.Builder, MysqlxCrud.Insert.TypedRowOrBuilder> getRowFieldBuilder() {
            if (this.rowBuilder_ == null) {
               this.rowBuilder_ = new RepeatedFieldBuilderV3<>(this.row_, (this.bitField0_ & 8) != 0, this.getParentForChildren(), this.isClean());
               this.row_ = null;
            }

            return this.rowBuilder_;
         }

         private void ensureArgsIsMutable() {
            if ((this.bitField0_ & 16) == 0) {
               this.args_ = new ArrayList<>(this.args_);
               this.bitField0_ |= 16;
            }
         }

         @Override
         public List<MysqlxDatatypes.Scalar> getArgsList() {
            return this.argsBuilder_ == null ? Collections.unmodifiableList(this.args_) : this.argsBuilder_.getMessageList();
         }

         @Override
         public int getArgsCount() {
            return this.argsBuilder_ == null ? this.args_.size() : this.argsBuilder_.getCount();
         }

         @Override
         public MysqlxDatatypes.Scalar getArgs(int index) {
            return this.argsBuilder_ == null ? this.args_.get(index) : this.argsBuilder_.getMessage(index);
         }

         public MysqlxCrud.Insert.Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.set(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addArgs(MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.args_);
               this.onChanged();
            } else {
               this.argsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder clearArgs() {
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -17;
               this.onChanged();
            } else {
               this.argsBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Insert.Builder removeArgs(int index) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.remove(index);
               this.onChanged();
            } else {
               this.argsBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
            return this.getArgsFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
            return this.argsBuilder_ == null ? this.args_.get(index) : this.argsBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
            return this.argsBuilder_ != null ? this.argsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.args_);
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
            return this.getArgsFieldBuilder().addBuilder(MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
            return this.getArgsFieldBuilder().addBuilder(index, MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
            return this.getArgsFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
            if (this.argsBuilder_ == null) {
               this.argsBuilder_ = new RepeatedFieldBuilderV3<>(this.args_, (this.bitField0_ & 16) != 0, this.getParentForChildren(), this.isClean());
               this.args_ = null;
            }

            return this.argsBuilder_;
         }

         @Override
         public boolean hasUpsert() {
            return (this.bitField0_ & 32) != 0;
         }

         @Override
         public boolean getUpsert() {
            return this.upsert_;
         }

         public MysqlxCrud.Insert.Builder setUpsert(boolean value) {
            this.bitField0_ |= 32;
            this.upsert_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxCrud.Insert.Builder clearUpsert() {
            this.bitField0_ &= -33;
            this.upsert_ = false;
            this.onChanged();
            return this;
         }

         public final MysqlxCrud.Insert.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Insert.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.Insert.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Insert.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static final class TypedRow extends GeneratedMessageV3 implements MysqlxCrud.Insert.TypedRowOrBuilder {
         private static final long serialVersionUID = 0L;
         public static final int FIELD_FIELD_NUMBER = 1;
         private List<MysqlxExpr.Expr> field_;
         private byte memoizedIsInitialized = -1;
         private static final MysqlxCrud.Insert.TypedRow DEFAULT_INSTANCE = new MysqlxCrud.Insert.TypedRow();
         @Deprecated
         public static final Parser<MysqlxCrud.Insert.TypedRow> PARSER = new AbstractParser<MysqlxCrud.Insert.TypedRow>() {
            public MysqlxCrud.Insert.TypedRow parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
               return new MysqlxCrud.Insert.TypedRow(input, extensionRegistry);
            }
         };

         private TypedRow(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
         }

         private TypedRow() {
            this.field_ = Collections.emptyList();
         }

         @Override
         protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new MysqlxCrud.Insert.TypedRow();
         }

         @Override
         public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
         }

         private TypedRow(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
               throw new NullPointerException();
            } else {
               int mutable_bitField0_ = 0;
               UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

               try {
                  boolean done = false;

                  while (!done) {
                     int tag = input.readTag();
                     switch (tag) {
                        case 0:
                           done = true;
                           break;
                        case 10:
                           if ((mutable_bitField0_ & 1) == 0) {
                              this.field_ = new ArrayList<>();
                              mutable_bitField0_ |= 1;
                           }

                           this.field_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry));
                           break;
                        default:
                           if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                              done = true;
                           }
                     }
                  }
               } catch (InvalidProtocolBufferException var11) {
                  throw var11.setUnfinishedMessage(this);
               } catch (IOException var12) {
                  throw new InvalidProtocolBufferException(var12).setUnfinishedMessage(this);
               } finally {
                  if ((mutable_bitField0_ & 1) != 0) {
                     this.field_ = Collections.unmodifiableList(this.field_);
                  }

                  this.unknownFields = unknownFields.build();
                  this.makeExtensionsImmutable();
               }
            }
         }

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.Insert.TypedRow.class, MysqlxCrud.Insert.TypedRow.Builder.class);
         }

         @Override
         public List<MysqlxExpr.Expr> getFieldList() {
            return this.field_;
         }

         @Override
         public List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList() {
            return this.field_;
         }

         @Override
         public int getFieldCount() {
            return this.field_.size();
         }

         @Override
         public MysqlxExpr.Expr getField(int index) {
            return this.field_.get(index);
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int index) {
            return this.field_.get(index);
         }

         @Override
         public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
               return true;
            } else if (isInitialized == 0) {
               return false;
            } else {
               for (int i = 0; i < this.getFieldCount(); i++) {
                  if (!this.getField(i).isInitialized()) {
                     this.memoizedIsInitialized = 0;
                     return false;
                  }
               }

               this.memoizedIsInitialized = 1;
               return true;
            }
         }

         @Override
         public void writeTo(CodedOutputStream output) throws IOException {
            for (int i = 0; i < this.field_.size(); i++) {
               output.writeMessage(1, this.field_.get(i));
            }

            this.unknownFields.writeTo(output);
         }

         @Override
         public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
               return size;
            } else {
               size = 0;

               for (int i = 0; i < this.field_.size(); i++) {
                  size += CodedOutputStream.computeMessageSize(1, this.field_.get(i));
               }

               size += this.unknownFields.getSerializedSize();
               this.memoizedSize = size;
               return size;
            }
         }

         @Override
         public boolean equals(Object obj) {
            if (obj == this) {
               return true;
            } else if (!(obj instanceof MysqlxCrud.Insert.TypedRow)) {
               return super.equals(obj);
            } else {
               MysqlxCrud.Insert.TypedRow other = (MysqlxCrud.Insert.TypedRow)obj;
               return !this.getFieldList().equals(other.getFieldList()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }

         @Override
         public int hashCode() {
            if (this.memoizedHashCode != 0) {
               return this.memoizedHashCode;
            } else {
               int hash = 41;
               hash = 19 * hash + getDescriptor().hashCode();
               if (this.getFieldCount() > 0) {
                  hash = 37 * hash + 1;
                  hash = 53 * hash + this.getFieldList().hashCode();
               }

               hash = 29 * hash + this.unknownFields.hashCode();
               this.memoizedHashCode = hash;
               return hash;
            }
         }

         public static MysqlxCrud.Insert.TypedRow parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxCrud.Insert.TypedRow parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxCrud.Insert.TypedRow parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxCrud.Insert.TypedRow parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxCrud.Insert.TypedRow parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxCrud.Insert.TypedRow parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxCrud.Insert.TypedRow parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static MysqlxCrud.Insert.TypedRow parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public static MysqlxCrud.Insert.TypedRow parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
         }

         public static MysqlxCrud.Insert.TypedRow parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
         }

         public static MysqlxCrud.Insert.TypedRow parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static MysqlxCrud.Insert.TypedRow parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public MysqlxCrud.Insert.TypedRow.Builder newBuilderForType() {
            return newBuilder();
         }

         public static MysqlxCrud.Insert.TypedRow.Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static MysqlxCrud.Insert.TypedRow.Builder newBuilder(MysqlxCrud.Insert.TypedRow prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
         }

         public MysqlxCrud.Insert.TypedRow.Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new MysqlxCrud.Insert.TypedRow.Builder() : new MysqlxCrud.Insert.TypedRow.Builder().mergeFrom(this);
         }

         protected MysqlxCrud.Insert.TypedRow.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            return new MysqlxCrud.Insert.TypedRow.Builder(parent);
         }

         public static MysqlxCrud.Insert.TypedRow getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<MysqlxCrud.Insert.TypedRow> parser() {
            return PARSER;
         }

         @Override
         public Parser<MysqlxCrud.Insert.TypedRow> getParserForType() {
            return PARSER;
         }

         public MysqlxCrud.Insert.TypedRow getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         public static final class Builder
            extends GeneratedMessageV3.Builder<MysqlxCrud.Insert.TypedRow.Builder>
            implements MysqlxCrud.Insert.TypedRowOrBuilder {
            private int bitField0_;
            private List<MysqlxExpr.Expr> field_ = Collections.emptyList();
            private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> fieldBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
               return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
               return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable
                  .ensureFieldAccessorsInitialized(MysqlxCrud.Insert.TypedRow.class, MysqlxCrud.Insert.TypedRow.Builder.class);
            }

            private Builder() {
               this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
               super(parent);
               this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
               if (MysqlxCrud.Insert.TypedRow.alwaysUseFieldBuilders) {
                  this.getFieldFieldBuilder();
               }
            }

            public MysqlxCrud.Insert.TypedRow.Builder clear() {
               super.clear();
               if (this.fieldBuilder_ == null) {
                  this.field_ = Collections.emptyList();
                  this.bitField0_ &= -2;
               } else {
                  this.fieldBuilder_.clear();
               }

               return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
               return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
            }

            public MysqlxCrud.Insert.TypedRow getDefaultInstanceForType() {
               return MysqlxCrud.Insert.TypedRow.getDefaultInstance();
            }

            public MysqlxCrud.Insert.TypedRow build() {
               MysqlxCrud.Insert.TypedRow result = this.buildPartial();
               if (!result.isInitialized()) {
                  throw newUninitializedMessageException(result);
               } else {
                  return result;
               }
            }

            public MysqlxCrud.Insert.TypedRow buildPartial() {
               MysqlxCrud.Insert.TypedRow result = new MysqlxCrud.Insert.TypedRow(this);
               int from_bitField0_ = this.bitField0_;
               if (this.fieldBuilder_ == null) {
                  if ((this.bitField0_ & 1) != 0) {
                     this.field_ = Collections.unmodifiableList(this.field_);
                     this.bitField0_ &= -2;
                  }

                  result.field_ = this.field_;
               } else {
                  result.field_ = this.fieldBuilder_.build();
               }

               this.onBuilt();
               return result;
            }

            public MysqlxCrud.Insert.TypedRow.Builder clone() {
               return (MysqlxCrud.Insert.TypedRow.Builder)super.clone();
            }

            public MysqlxCrud.Insert.TypedRow.Builder setField(Descriptors.FieldDescriptor field, Object value) {
               return (MysqlxCrud.Insert.TypedRow.Builder)super.setField(field, value);
            }

            public MysqlxCrud.Insert.TypedRow.Builder clearField(Descriptors.FieldDescriptor field) {
               return (MysqlxCrud.Insert.TypedRow.Builder)super.clearField(field);
            }

            public MysqlxCrud.Insert.TypedRow.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
               return (MysqlxCrud.Insert.TypedRow.Builder)super.clearOneof(oneof);
            }

            public MysqlxCrud.Insert.TypedRow.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
               return (MysqlxCrud.Insert.TypedRow.Builder)super.setRepeatedField(field, index, value);
            }

            public MysqlxCrud.Insert.TypedRow.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
               return (MysqlxCrud.Insert.TypedRow.Builder)super.addRepeatedField(field, value);
            }

            public MysqlxCrud.Insert.TypedRow.Builder mergeFrom(Message other) {
               if (other instanceof MysqlxCrud.Insert.TypedRow) {
                  return this.mergeFrom((MysqlxCrud.Insert.TypedRow)other);
               } else {
                  super.mergeFrom(other);
                  return this;
               }
            }

            public MysqlxCrud.Insert.TypedRow.Builder mergeFrom(MysqlxCrud.Insert.TypedRow other) {
               if (other == MysqlxCrud.Insert.TypedRow.getDefaultInstance()) {
                  return this;
               } else {
                  if (this.fieldBuilder_ == null) {
                     if (!other.field_.isEmpty()) {
                        if (this.field_.isEmpty()) {
                           this.field_ = other.field_;
                           this.bitField0_ &= -2;
                        } else {
                           this.ensureFieldIsMutable();
                           this.field_.addAll(other.field_);
                        }

                        this.onChanged();
                     }
                  } else if (!other.field_.isEmpty()) {
                     if (this.fieldBuilder_.isEmpty()) {
                        this.fieldBuilder_.dispose();
                        this.fieldBuilder_ = null;
                        this.field_ = other.field_;
                        this.bitField0_ &= -2;
                        this.fieldBuilder_ = MysqlxCrud.Insert.TypedRow.alwaysUseFieldBuilders ? this.getFieldFieldBuilder() : null;
                     } else {
                        this.fieldBuilder_.addAllMessages(other.field_);
                     }
                  }

                  this.mergeUnknownFields(other.unknownFields);
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public final boolean isInitialized() {
               for (int i = 0; i < this.getFieldCount(); i++) {
                  if (!this.getField(i).isInitialized()) {
                     return false;
                  }
               }

               return true;
            }

            public MysqlxCrud.Insert.TypedRow.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
               MysqlxCrud.Insert.TypedRow parsedMessage = null;

               try {
                  parsedMessage = MysqlxCrud.Insert.TypedRow.PARSER.parsePartialFrom(input, extensionRegistry);
               } catch (InvalidProtocolBufferException var8) {
                  parsedMessage = (MysqlxCrud.Insert.TypedRow)var8.getUnfinishedMessage();
                  throw var8.unwrapIOException();
               } finally {
                  if (parsedMessage != null) {
                     this.mergeFrom(parsedMessage);
                  }
               }

               return this;
            }

            private void ensureFieldIsMutable() {
               if ((this.bitField0_ & 1) == 0) {
                  this.field_ = new ArrayList<>(this.field_);
                  this.bitField0_ |= 1;
               }
            }

            @Override
            public List<MysqlxExpr.Expr> getFieldList() {
               return this.fieldBuilder_ == null ? Collections.unmodifiableList(this.field_) : this.fieldBuilder_.getMessageList();
            }

            @Override
            public int getFieldCount() {
               return this.fieldBuilder_ == null ? this.field_.size() : this.fieldBuilder_.getCount();
            }

            @Override
            public MysqlxExpr.Expr getField(int index) {
               return this.fieldBuilder_ == null ? this.field_.get(index) : this.fieldBuilder_.getMessage(index);
            }

            public MysqlxCrud.Insert.TypedRow.Builder setField(int index, MysqlxExpr.Expr value) {
               if (this.fieldBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.ensureFieldIsMutable();
                  this.field_.set(index, value);
                  this.onChanged();
               } else {
                  this.fieldBuilder_.setMessage(index, value);
               }

               return this;
            }

            public MysqlxCrud.Insert.TypedRow.Builder setField(int index, MysqlxExpr.Expr.Builder builderForValue) {
               if (this.fieldBuilder_ == null) {
                  this.ensureFieldIsMutable();
                  this.field_.set(index, builderForValue.build());
                  this.onChanged();
               } else {
                  this.fieldBuilder_.setMessage(index, builderForValue.build());
               }

               return this;
            }

            public MysqlxCrud.Insert.TypedRow.Builder addField(MysqlxExpr.Expr value) {
               if (this.fieldBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.ensureFieldIsMutable();
                  this.field_.add(value);
                  this.onChanged();
               } else {
                  this.fieldBuilder_.addMessage(value);
               }

               return this;
            }

            public MysqlxCrud.Insert.TypedRow.Builder addField(int index, MysqlxExpr.Expr value) {
               if (this.fieldBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.ensureFieldIsMutable();
                  this.field_.add(index, value);
                  this.onChanged();
               } else {
                  this.fieldBuilder_.addMessage(index, value);
               }

               return this;
            }

            public MysqlxCrud.Insert.TypedRow.Builder addField(MysqlxExpr.Expr.Builder builderForValue) {
               if (this.fieldBuilder_ == null) {
                  this.ensureFieldIsMutable();
                  this.field_.add(builderForValue.build());
                  this.onChanged();
               } else {
                  this.fieldBuilder_.addMessage(builderForValue.build());
               }

               return this;
            }

            public MysqlxCrud.Insert.TypedRow.Builder addField(int index, MysqlxExpr.Expr.Builder builderForValue) {
               if (this.fieldBuilder_ == null) {
                  this.ensureFieldIsMutable();
                  this.field_.add(index, builderForValue.build());
                  this.onChanged();
               } else {
                  this.fieldBuilder_.addMessage(index, builderForValue.build());
               }

               return this;
            }

            public MysqlxCrud.Insert.TypedRow.Builder addAllField(Iterable<? extends MysqlxExpr.Expr> values) {
               if (this.fieldBuilder_ == null) {
                  this.ensureFieldIsMutable();
                  AbstractMessageLite.Builder.addAll(values, this.field_);
                  this.onChanged();
               } else {
                  this.fieldBuilder_.addAllMessages(values);
               }

               return this;
            }

            public MysqlxCrud.Insert.TypedRow.Builder clearField() {
               if (this.fieldBuilder_ == null) {
                  this.field_ = Collections.emptyList();
                  this.bitField0_ &= -2;
                  this.onChanged();
               } else {
                  this.fieldBuilder_.clear();
               }

               return this;
            }

            public MysqlxCrud.Insert.TypedRow.Builder removeField(int index) {
               if (this.fieldBuilder_ == null) {
                  this.ensureFieldIsMutable();
                  this.field_.remove(index);
                  this.onChanged();
               } else {
                  this.fieldBuilder_.remove(index);
               }

               return this;
            }

            public MysqlxExpr.Expr.Builder getFieldBuilder(int index) {
               return this.getFieldFieldBuilder().getBuilder(index);
            }

            @Override
            public MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int index) {
               return this.fieldBuilder_ == null ? this.field_.get(index) : this.fieldBuilder_.getMessageOrBuilder(index);
            }

            @Override
            public List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList() {
               return this.fieldBuilder_ != null ? this.fieldBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.field_);
            }

            public MysqlxExpr.Expr.Builder addFieldBuilder() {
               return this.getFieldFieldBuilder().addBuilder(MysqlxExpr.Expr.getDefaultInstance());
            }

            public MysqlxExpr.Expr.Builder addFieldBuilder(int index) {
               return this.getFieldFieldBuilder().addBuilder(index, MysqlxExpr.Expr.getDefaultInstance());
            }

            public List<MysqlxExpr.Expr.Builder> getFieldBuilderList() {
               return this.getFieldFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getFieldFieldBuilder() {
               if (this.fieldBuilder_ == null) {
                  this.fieldBuilder_ = new RepeatedFieldBuilderV3<>(this.field_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
                  this.field_ = null;
               }

               return this.fieldBuilder_;
            }

            public final MysqlxCrud.Insert.TypedRow.Builder setUnknownFields(UnknownFieldSet unknownFields) {
               return (MysqlxCrud.Insert.TypedRow.Builder)super.setUnknownFields(unknownFields);
            }

            public final MysqlxCrud.Insert.TypedRow.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
               return (MysqlxCrud.Insert.TypedRow.Builder)super.mergeUnknownFields(unknownFields);
            }
         }
      }

      public interface TypedRowOrBuilder extends MessageOrBuilder {
         List<MysqlxExpr.Expr> getFieldList();

         MysqlxExpr.Expr getField(int var1);

         int getFieldCount();

         List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList();

         MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int var1);
      }
   }

   public interface InsertOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      MysqlxCrud.Collection getCollection();

      MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDataModel();

      MysqlxCrud.DataModel getDataModel();

      List<MysqlxCrud.Column> getProjectionList();

      MysqlxCrud.Column getProjection(int var1);

      int getProjectionCount();

      List<? extends MysqlxCrud.ColumnOrBuilder> getProjectionOrBuilderList();

      MysqlxCrud.ColumnOrBuilder getProjectionOrBuilder(int var1);

      List<MysqlxCrud.Insert.TypedRow> getRowList();

      MysqlxCrud.Insert.TypedRow getRow(int var1);

      int getRowCount();

      List<? extends MysqlxCrud.Insert.TypedRowOrBuilder> getRowOrBuilderList();

      MysqlxCrud.Insert.TypedRowOrBuilder getRowOrBuilder(int var1);

      List<MysqlxDatatypes.Scalar> getArgsList();

      MysqlxDatatypes.Scalar getArgs(int var1);

      int getArgsCount();

      List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();

      MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int var1);

      boolean hasUpsert();

      boolean getUpsert();
   }

   public static final class Limit extends GeneratedMessageV3 implements MysqlxCrud.LimitOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int ROW_COUNT_FIELD_NUMBER = 1;
      private long rowCount_;
      public static final int OFFSET_FIELD_NUMBER = 2;
      private long offset_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.Limit DEFAULT_INSTANCE = new MysqlxCrud.Limit();
      @Deprecated
      public static final Parser<MysqlxCrud.Limit> PARSER = new AbstractParser<MysqlxCrud.Limit>() {
         public MysqlxCrud.Limit parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.Limit(input, extensionRegistry);
         }
      };

      private Limit(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Limit() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.Limit();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Limit(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 8:
                        this.bitField0_ |= 1;
                        this.rowCount_ = input.readUInt64();
                        break;
                     case 16:
                        this.bitField0_ |= 2;
                        this.offset_ = input.readUInt64();
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var11) {
               throw var11.setUnfinishedMessage(this);
            } catch (IOException var12) {
               throw new InvalidProtocolBufferException(var12).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.Limit.class, MysqlxCrud.Limit.Builder.class);
      }

      @Override
      public boolean hasRowCount() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public long getRowCount() {
         return this.rowCount_;
      }

      @Override
      public boolean hasOffset() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public long getOffset() {
         return this.offset_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasRowCount()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeUInt64(1, this.rowCount_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeUInt64(2, this.offset_);
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeUInt64Size(1, this.rowCount_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeUInt64Size(2, this.offset_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.Limit)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.Limit other = (MysqlxCrud.Limit)obj;
            if (this.hasRowCount() != other.hasRowCount()) {
               return false;
            } else if (this.hasRowCount() && this.getRowCount() != other.getRowCount()) {
               return false;
            } else if (this.hasOffset() != other.hasOffset()) {
               return false;
            } else {
               return this.hasOffset() && this.getOffset() != other.getOffset() ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasRowCount()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + Internal.hashLong(this.getRowCount());
            }

            if (this.hasOffset()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + Internal.hashLong(this.getOffset());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.Limit parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Limit parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Limit parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Limit parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Limit parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Limit parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Limit parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Limit parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Limit parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Limit parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Limit parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Limit parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.Limit.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.Limit.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.Limit.Builder newBuilder(MysqlxCrud.Limit prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.Limit.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.Limit.Builder() : new MysqlxCrud.Limit.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.Limit.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.Limit.Builder(parent);
      }

      public static MysqlxCrud.Limit getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.Limit> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.Limit> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.Limit getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.Limit.Builder> implements MysqlxCrud.LimitOrBuilder {
         private int bitField0_;
         private long rowCount_;
         private long offset_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.Limit.class, MysqlxCrud.Limit.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Limit.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxCrud.Limit.Builder clear() {
            super.clear();
            this.rowCount_ = 0L;
            this.bitField0_ &= -2;
            this.offset_ = 0L;
            this.bitField0_ &= -3;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor;
         }

         public MysqlxCrud.Limit getDefaultInstanceForType() {
            return MysqlxCrud.Limit.getDefaultInstance();
         }

         public MysqlxCrud.Limit build() {
            MysqlxCrud.Limit result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.Limit buildPartial() {
            MysqlxCrud.Limit result = new MysqlxCrud.Limit(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.rowCount_ = this.rowCount_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               result.offset_ = this.offset_;
               to_bitField0_ |= 2;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.Limit.Builder clone() {
            return (MysqlxCrud.Limit.Builder)super.clone();
         }

         public MysqlxCrud.Limit.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Limit.Builder)super.setField(field, value);
         }

         public MysqlxCrud.Limit.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.Limit.Builder)super.clearField(field);
         }

         public MysqlxCrud.Limit.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.Limit.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.Limit.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.Limit.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.Limit.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Limit.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.Limit.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.Limit) {
               return this.mergeFrom((MysqlxCrud.Limit)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.Limit.Builder mergeFrom(MysqlxCrud.Limit other) {
            if (other == MysqlxCrud.Limit.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasRowCount()) {
                  this.setRowCount(other.getRowCount());
               }

               if (other.hasOffset()) {
                  this.setOffset(other.getOffset());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return this.hasRowCount();
         }

         public MysqlxCrud.Limit.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.Limit parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.Limit.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.Limit)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasRowCount() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public long getRowCount() {
            return this.rowCount_;
         }

         public MysqlxCrud.Limit.Builder setRowCount(long value) {
            this.bitField0_ |= 1;
            this.rowCount_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxCrud.Limit.Builder clearRowCount() {
            this.bitField0_ &= -2;
            this.rowCount_ = 0L;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasOffset() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public long getOffset() {
            return this.offset_;
         }

         public MysqlxCrud.Limit.Builder setOffset(long value) {
            this.bitField0_ |= 2;
            this.offset_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxCrud.Limit.Builder clearOffset() {
            this.bitField0_ &= -3;
            this.offset_ = 0L;
            this.onChanged();
            return this;
         }

         public final MysqlxCrud.Limit.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Limit.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.Limit.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Limit.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public static final class LimitExpr extends GeneratedMessageV3 implements MysqlxCrud.LimitExprOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int ROW_COUNT_FIELD_NUMBER = 1;
      private MysqlxExpr.Expr rowCount_;
      public static final int OFFSET_FIELD_NUMBER = 2;
      private MysqlxExpr.Expr offset_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.LimitExpr DEFAULT_INSTANCE = new MysqlxCrud.LimitExpr();
      @Deprecated
      public static final Parser<MysqlxCrud.LimitExpr> PARSER = new AbstractParser<MysqlxCrud.LimitExpr>() {
         public MysqlxCrud.LimitExpr parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.LimitExpr(input, extensionRegistry);
         }
      };

      private LimitExpr(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private LimitExpr() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.LimitExpr();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private LimitExpr(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxExpr.Expr.Builder subBuilderx = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilderx = this.rowCount_.toBuilder();
                        }

                        this.rowCount_ = input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilderx != null) {
                           subBuilderx.mergeFrom(this.rowCount_);
                           this.rowCount_ = subBuilderx.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        MysqlxExpr.Expr.Builder subBuilder = null;
                        if ((this.bitField0_ & 2) != 0) {
                           subBuilder = this.offset_.toBuilder();
                        }

                        this.offset_ = input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.offset_);
                           this.offset_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 2;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.LimitExpr.class, MysqlxCrud.LimitExpr.Builder.class);
      }

      @Override
      public boolean hasRowCount() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxExpr.Expr getRowCount() {
         return this.rowCount_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getRowCountOrBuilder() {
         return this.rowCount_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
      }

      @Override
      public boolean hasOffset() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public MysqlxExpr.Expr getOffset() {
         return this.offset_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getOffsetOrBuilder() {
         return this.offset_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasRowCount()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getRowCount().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasOffset() && !this.getOffset().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getRowCount());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeMessage(2, this.getOffset());
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getRowCount());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeMessageSize(2, this.getOffset());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.LimitExpr)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.LimitExpr other = (MysqlxCrud.LimitExpr)obj;
            if (this.hasRowCount() != other.hasRowCount()) {
               return false;
            } else if (this.hasRowCount() && !this.getRowCount().equals(other.getRowCount())) {
               return false;
            } else if (this.hasOffset() != other.hasOffset()) {
               return false;
            } else {
               return this.hasOffset() && !this.getOffset().equals(other.getOffset()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasRowCount()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getRowCount().hashCode();
            }

            if (this.hasOffset()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getOffset().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.LimitExpr parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.LimitExpr parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.LimitExpr parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.LimitExpr parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.LimitExpr parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.LimitExpr parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.LimitExpr parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.LimitExpr parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.LimitExpr parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.LimitExpr parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.LimitExpr parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.LimitExpr parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.LimitExpr.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.LimitExpr.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.LimitExpr.Builder newBuilder(MysqlxCrud.LimitExpr prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.LimitExpr.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.LimitExpr.Builder() : new MysqlxCrud.LimitExpr.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.LimitExpr.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.LimitExpr.Builder(parent);
      }

      public static MysqlxCrud.LimitExpr getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.LimitExpr> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.LimitExpr> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.LimitExpr getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.LimitExpr.Builder> implements MysqlxCrud.LimitExprOrBuilder {
         private int bitField0_;
         private MysqlxExpr.Expr rowCount_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> rowCountBuilder_;
         private MysqlxExpr.Expr offset_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> offsetBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.LimitExpr.class, MysqlxCrud.LimitExpr.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.LimitExpr.alwaysUseFieldBuilders) {
               this.getRowCountFieldBuilder();
               this.getOffsetFieldBuilder();
            }
         }

         public MysqlxCrud.LimitExpr.Builder clear() {
            super.clear();
            if (this.rowCountBuilder_ == null) {
               this.rowCount_ = null;
            } else {
               this.rowCountBuilder_.clear();
            }

            this.bitField0_ &= -2;
            if (this.offsetBuilder_ == null) {
               this.offset_ = null;
            } else {
               this.offsetBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor;
         }

         public MysqlxCrud.LimitExpr getDefaultInstanceForType() {
            return MysqlxCrud.LimitExpr.getDefaultInstance();
         }

         public MysqlxCrud.LimitExpr build() {
            MysqlxCrud.LimitExpr result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.LimitExpr buildPartial() {
            MysqlxCrud.LimitExpr result = new MysqlxCrud.LimitExpr(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.rowCountBuilder_ == null) {
                  result.rowCount_ = this.rowCount_;
               } else {
                  result.rowCount_ = this.rowCountBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               if (this.offsetBuilder_ == null) {
                  result.offset_ = this.offset_;
               } else {
                  result.offset_ = this.offsetBuilder_.build();
               }

               to_bitField0_ |= 2;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.LimitExpr.Builder clone() {
            return (MysqlxCrud.LimitExpr.Builder)super.clone();
         }

         public MysqlxCrud.LimitExpr.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.LimitExpr.Builder)super.setField(field, value);
         }

         public MysqlxCrud.LimitExpr.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.LimitExpr.Builder)super.clearField(field);
         }

         public MysqlxCrud.LimitExpr.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.LimitExpr.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.LimitExpr.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.LimitExpr.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.LimitExpr.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.LimitExpr.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.LimitExpr.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.LimitExpr) {
               return this.mergeFrom((MysqlxCrud.LimitExpr)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.LimitExpr.Builder mergeFrom(MysqlxCrud.LimitExpr other) {
            if (other == MysqlxCrud.LimitExpr.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasRowCount()) {
                  this.mergeRowCount(other.getRowCount());
               }

               if (other.hasOffset()) {
                  this.mergeOffset(other.getOffset());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasRowCount()) {
               return false;
            } else {
               return !this.getRowCount().isInitialized() ? false : !this.hasOffset() || this.getOffset().isInitialized();
            }
         }

         public MysqlxCrud.LimitExpr.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.LimitExpr parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.LimitExpr.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.LimitExpr)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasRowCount() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxExpr.Expr getRowCount() {
            if (this.rowCountBuilder_ == null) {
               return this.rowCount_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
            } else {
               return this.rowCountBuilder_.getMessage();
            }
         }

         public MysqlxCrud.LimitExpr.Builder setRowCount(MysqlxExpr.Expr value) {
            if (this.rowCountBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.rowCount_ = value;
               this.onChanged();
            } else {
               this.rowCountBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.LimitExpr.Builder setRowCount(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.rowCountBuilder_ == null) {
               this.rowCount_ = builderForValue.build();
               this.onChanged();
            } else {
               this.rowCountBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.LimitExpr.Builder mergeRowCount(MysqlxExpr.Expr value) {
            if (this.rowCountBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.rowCount_ != null && this.rowCount_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.rowCount_ = MysqlxExpr.Expr.newBuilder(this.rowCount_).mergeFrom(value).buildPartial();
               } else {
                  this.rowCount_ = value;
               }

               this.onChanged();
            } else {
               this.rowCountBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.LimitExpr.Builder clearRowCount() {
            if (this.rowCountBuilder_ == null) {
               this.rowCount_ = null;
               this.onChanged();
            } else {
               this.rowCountBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxExpr.Expr.Builder getRowCountBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getRowCountFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getRowCountOrBuilder() {
            if (this.rowCountBuilder_ != null) {
               return this.rowCountBuilder_.getMessageOrBuilder();
            } else {
               return this.rowCount_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getRowCountFieldBuilder() {
            if (this.rowCountBuilder_ == null) {
               this.rowCountBuilder_ = new SingleFieldBuilderV3<>(this.getRowCount(), this.getParentForChildren(), this.isClean());
               this.rowCount_ = null;
            }

            return this.rowCountBuilder_;
         }

         @Override
         public boolean hasOffset() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxExpr.Expr getOffset() {
            if (this.offsetBuilder_ == null) {
               return this.offset_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
            } else {
               return this.offsetBuilder_.getMessage();
            }
         }

         public MysqlxCrud.LimitExpr.Builder setOffset(MysqlxExpr.Expr value) {
            if (this.offsetBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.offset_ = value;
               this.onChanged();
            } else {
               this.offsetBuilder_.setMessage(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxCrud.LimitExpr.Builder setOffset(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.offsetBuilder_ == null) {
               this.offset_ = builderForValue.build();
               this.onChanged();
            } else {
               this.offsetBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxCrud.LimitExpr.Builder mergeOffset(MysqlxExpr.Expr value) {
            if (this.offsetBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0 && this.offset_ != null && this.offset_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.offset_ = MysqlxExpr.Expr.newBuilder(this.offset_).mergeFrom(value).buildPartial();
               } else {
                  this.offset_ = value;
               }

               this.onChanged();
            } else {
               this.offsetBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxCrud.LimitExpr.Builder clearOffset() {
            if (this.offsetBuilder_ == null) {
               this.offset_ = null;
               this.onChanged();
            } else {
               this.offsetBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public MysqlxExpr.Expr.Builder getOffsetBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.getOffsetFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getOffsetOrBuilder() {
            if (this.offsetBuilder_ != null) {
               return this.offsetBuilder_.getMessageOrBuilder();
            } else {
               return this.offset_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getOffsetFieldBuilder() {
            if (this.offsetBuilder_ == null) {
               this.offsetBuilder_ = new SingleFieldBuilderV3<>(this.getOffset(), this.getParentForChildren(), this.isClean());
               this.offset_ = null;
            }

            return this.offsetBuilder_;
         }

         public final MysqlxCrud.LimitExpr.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.LimitExpr.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.LimitExpr.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.LimitExpr.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface LimitExprOrBuilder extends MessageOrBuilder {
      boolean hasRowCount();

      MysqlxExpr.Expr getRowCount();

      MysqlxExpr.ExprOrBuilder getRowCountOrBuilder();

      boolean hasOffset();

      MysqlxExpr.Expr getOffset();

      MysqlxExpr.ExprOrBuilder getOffsetOrBuilder();
   }

   public interface LimitOrBuilder extends MessageOrBuilder {
      boolean hasRowCount();

      long getRowCount();

      boolean hasOffset();

      long getOffset();
   }

   public static final class ModifyView extends GeneratedMessageV3 implements MysqlxCrud.ModifyViewOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 1;
      private MysqlxCrud.Collection collection_;
      public static final int DEFINER_FIELD_NUMBER = 2;
      private volatile Object definer_;
      public static final int ALGORITHM_FIELD_NUMBER = 3;
      private int algorithm_;
      public static final int SECURITY_FIELD_NUMBER = 4;
      private int security_;
      public static final int CHECK_FIELD_NUMBER = 5;
      private int check_;
      public static final int COLUMN_FIELD_NUMBER = 6;
      private LazyStringList column_;
      public static final int STMT_FIELD_NUMBER = 7;
      private MysqlxCrud.Find stmt_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.ModifyView DEFAULT_INSTANCE = new MysqlxCrud.ModifyView();
      @Deprecated
      public static final Parser<MysqlxCrud.ModifyView> PARSER = new AbstractParser<MysqlxCrud.ModifyView>() {
         public MysqlxCrud.ModifyView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.ModifyView(input, extensionRegistry);
         }
      };

      private ModifyView(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private ModifyView() {
         this.definer_ = "";
         this.algorithm_ = 1;
         this.security_ = 1;
         this.check_ = 1;
         this.column_ = LazyStringArrayList.EMPTY;
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.ModifyView();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private ModifyView(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxCrud.Collection.Builder subBuilderx = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilderx = this.collection_.toBuilder();
                        }

                        this.collection_ = input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilderx != null) {
                           subBuilderx.mergeFrom(this.collection_);
                           this.collection_ = subBuilderx.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 18: {
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.definer_ = bs;
                        break;
                     }
                     case 24:
                        int rawValuexx = input.readEnum();
                        MysqlxCrud.ViewAlgorithm valuexx = MysqlxCrud.ViewAlgorithm.valueOf(rawValuexx);
                        if (valuexx == null) {
                           unknownFields.mergeVarintField(3, rawValuexx);
                        } else {
                           this.bitField0_ |= 4;
                           this.algorithm_ = rawValuexx;
                        }
                        break;
                     case 32:
                        int rawValuex = input.readEnum();
                        MysqlxCrud.ViewSqlSecurity valuex = MysqlxCrud.ViewSqlSecurity.valueOf(rawValuex);
                        if (valuex == null) {
                           unknownFields.mergeVarintField(4, rawValuex);
                        } else {
                           this.bitField0_ |= 8;
                           this.security_ = rawValuex;
                        }
                        break;
                     case 40:
                        int rawValue = input.readEnum();
                        MysqlxCrud.ViewCheckOption value = MysqlxCrud.ViewCheckOption.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(5, rawValue);
                        } else {
                           this.bitField0_ |= 16;
                           this.check_ = rawValue;
                        }
                        break;
                     case 50: {
                        ByteString bs = input.readBytes();
                        if ((mutable_bitField0_ & 32) == 0) {
                           this.column_ = new LazyStringArrayList();
                           mutable_bitField0_ |= 32;
                        }

                        this.column_.add(bs);
                        break;
                     }
                     case 58:
                        MysqlxCrud.Find.Builder subBuilder = null;
                        if ((this.bitField0_ & 32) != 0) {
                           subBuilder = this.stmt_.toBuilder();
                        }

                        this.stmt_ = input.readMessage(MysqlxCrud.Find.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.stmt_);
                           this.stmt_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 32;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw new InvalidProtocolBufferException(var14).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 32) != 0) {
                  this.column_ = this.column_.getUnmodifiableView();
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.ModifyView.class, MysqlxCrud.ModifyView.Builder.class);
      }

      @Override
      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxCrud.Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public boolean hasDefiner() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getDefiner() {
         Object ref = this.definer_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.definer_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getDefinerBytes() {
         Object ref = this.definer_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.definer_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasAlgorithm() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public MysqlxCrud.ViewAlgorithm getAlgorithm() {
         MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
         return result == null ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
      }

      @Override
      public boolean hasSecurity() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public MysqlxCrud.ViewSqlSecurity getSecurity() {
         MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
         return result == null ? MysqlxCrud.ViewSqlSecurity.INVOKER : result;
      }

      @Override
      public boolean hasCheck() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public MysqlxCrud.ViewCheckOption getCheck() {
         MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
         return result == null ? MysqlxCrud.ViewCheckOption.LOCAL : result;
      }

      public ProtocolStringList getColumnList() {
         return this.column_;
      }

      @Override
      public int getColumnCount() {
         return this.column_.size();
      }

      @Override
      public String getColumn(int index) {
         return this.column_.get(index);
      }

      @Override
      public ByteString getColumnBytes(int index) {
         return this.column_.getByteString(index);
      }

      @Override
      public boolean hasStmt() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public MysqlxCrud.Find getStmt() {
         return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
      }

      @Override
      public MysqlxCrud.FindOrBuilder getStmtOrBuilder() {
         return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasStmt() && !this.getStmt().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.definer_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeEnum(3, this.algorithm_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeEnum(4, this.security_);
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeEnum(5, this.check_);
         }

         for (int i = 0; i < this.column_.size(); i++) {
            GeneratedMessageV3.writeString(output, 6, this.column_.getRaw(i));
         }

         if ((this.bitField0_ & 32) != 0) {
            output.writeMessage(7, this.getStmt());
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.definer_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeEnumSize(3, this.algorithm_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeEnumSize(4, this.security_);
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeEnumSize(5, this.check_);
            }

            int dataSize = 0;

            for (int i = 0; i < this.column_.size(); i++) {
               dataSize += computeStringSizeNoTag(this.column_.getRaw(i));
            }

            size += dataSize;
            size += 1 * this.getColumnList().size();
            if ((this.bitField0_ & 32) != 0) {
               size += CodedOutputStream.computeMessageSize(7, this.getStmt());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.ModifyView)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.ModifyView other = (MysqlxCrud.ModifyView)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDefiner() != other.hasDefiner()) {
               return false;
            } else if (this.hasDefiner() && !this.getDefiner().equals(other.getDefiner())) {
               return false;
            } else if (this.hasAlgorithm() != other.hasAlgorithm()) {
               return false;
            } else if (this.hasAlgorithm() && this.algorithm_ != other.algorithm_) {
               return false;
            } else if (this.hasSecurity() != other.hasSecurity()) {
               return false;
            } else if (this.hasSecurity() && this.security_ != other.security_) {
               return false;
            } else if (this.hasCheck() != other.hasCheck()) {
               return false;
            } else if (this.hasCheck() && this.check_ != other.check_) {
               return false;
            } else if (!this.getColumnList().equals(other.getColumnList())) {
               return false;
            } else if (this.hasStmt() != other.hasStmt()) {
               return false;
            } else {
               return this.hasStmt() && !this.getStmt().equals(other.getStmt()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDefiner()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getDefiner().hashCode();
            }

            if (this.hasAlgorithm()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.algorithm_;
            }

            if (this.hasSecurity()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.security_;
            }

            if (this.hasCheck()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.check_;
            }

            if (this.getColumnCount() > 0) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getColumnList().hashCode();
            }

            if (this.hasStmt()) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getStmt().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.ModifyView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.ModifyView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.ModifyView parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.ModifyView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.ModifyView parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.ModifyView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.ModifyView parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.ModifyView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.ModifyView parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.ModifyView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.ModifyView parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.ModifyView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.ModifyView.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.ModifyView.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.ModifyView.Builder newBuilder(MysqlxCrud.ModifyView prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.ModifyView.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.ModifyView.Builder() : new MysqlxCrud.ModifyView.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.ModifyView.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.ModifyView.Builder(parent);
      }

      public static MysqlxCrud.ModifyView getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.ModifyView> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.ModifyView> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.ModifyView getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.ModifyView.Builder> implements MysqlxCrud.ModifyViewOrBuilder {
         private int bitField0_;
         private MysqlxCrud.Collection collection_;
         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
         private Object definer_ = "";
         private int algorithm_ = 1;
         private int security_ = 1;
         private int check_ = 1;
         private LazyStringList column_ = LazyStringArrayList.EMPTY;
         private MysqlxCrud.Find stmt_;
         private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> stmtBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.ModifyView.class, MysqlxCrud.ModifyView.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.ModifyView.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getStmtFieldBuilder();
            }
         }

         public MysqlxCrud.ModifyView.Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.definer_ = "";
            this.bitField0_ &= -3;
            this.algorithm_ = 1;
            this.bitField0_ &= -5;
            this.security_ = 1;
            this.bitField0_ &= -9;
            this.check_ = 1;
            this.bitField0_ &= -17;
            this.column_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= -33;
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -65;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor;
         }

         public MysqlxCrud.ModifyView getDefaultInstanceForType() {
            return MysqlxCrud.ModifyView.getDefaultInstance();
         }

         public MysqlxCrud.ModifyView build() {
            MysqlxCrud.ModifyView result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.ModifyView buildPartial() {
            MysqlxCrud.ModifyView result = new MysqlxCrud.ModifyView(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.definer_ = this.definer_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.algorithm_ = this.algorithm_;
            if ((from_bitField0_ & 8) != 0) {
               to_bitField0_ |= 8;
            }

            result.security_ = this.security_;
            if ((from_bitField0_ & 16) != 0) {
               to_bitField0_ |= 16;
            }

            result.check_ = this.check_;
            if ((this.bitField0_ & 32) != 0) {
               this.column_ = this.column_.getUnmodifiableView();
               this.bitField0_ &= -33;
            }

            result.column_ = this.column_;
            if ((from_bitField0_ & 64) != 0) {
               if (this.stmtBuilder_ == null) {
                  result.stmt_ = this.stmt_;
               } else {
                  result.stmt_ = this.stmtBuilder_.build();
               }

               to_bitField0_ |= 32;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.ModifyView.Builder clone() {
            return (MysqlxCrud.ModifyView.Builder)super.clone();
         }

         public MysqlxCrud.ModifyView.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.ModifyView.Builder)super.setField(field, value);
         }

         public MysqlxCrud.ModifyView.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.ModifyView.Builder)super.clearField(field);
         }

         public MysqlxCrud.ModifyView.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.ModifyView.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.ModifyView.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.ModifyView.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.ModifyView.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.ModifyView.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.ModifyView.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.ModifyView) {
               return this.mergeFrom((MysqlxCrud.ModifyView)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.ModifyView.Builder mergeFrom(MysqlxCrud.ModifyView other) {
            if (other == MysqlxCrud.ModifyView.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDefiner()) {
                  this.bitField0_ |= 2;
                  this.definer_ = other.definer_;
                  this.onChanged();
               }

               if (other.hasAlgorithm()) {
                  this.setAlgorithm(other.getAlgorithm());
               }

               if (other.hasSecurity()) {
                  this.setSecurity(other.getSecurity());
               }

               if (other.hasCheck()) {
                  this.setCheck(other.getCheck());
               }

               if (!other.column_.isEmpty()) {
                  if (this.column_.isEmpty()) {
                     this.column_ = other.column_;
                     this.bitField0_ &= -33;
                  } else {
                     this.ensureColumnIsMutable();
                     this.column_.addAll(other.column_);
                  }

                  this.onChanged();
               }

               if (other.hasStmt()) {
                  this.mergeStmt(other.getStmt());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else {
               return !this.getCollection().isInitialized() ? false : !this.hasStmt() || this.getStmt().isInitialized();
            }
         }

         public MysqlxCrud.ModifyView.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.ModifyView parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.ModifyView.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.ModifyView)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxCrud.Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return this.collectionBuilder_.getMessage();
            }
         }

         public MysqlxCrud.ModifyView.Builder setCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.ModifyView.Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.ModifyView.Builder mergeCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.ModifyView.Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxCrud.Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getCollectionFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3<>(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         @Override
         public boolean hasDefiner() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getDefiner() {
            Object ref = this.definer_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.definer_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getDefinerBytes() {
            Object ref = this.definer_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.definer_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxCrud.ModifyView.Builder setDefiner(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.definer_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.ModifyView.Builder clearDefiner() {
            this.bitField0_ &= -3;
            this.definer_ = MysqlxCrud.ModifyView.getDefaultInstance().getDefiner();
            this.onChanged();
            return this;
         }

         public MysqlxCrud.ModifyView.Builder setDefinerBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.definer_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasAlgorithm() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public MysqlxCrud.ViewAlgorithm getAlgorithm() {
            MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
            return result == null ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
         }

         public MysqlxCrud.ModifyView.Builder setAlgorithm(MysqlxCrud.ViewAlgorithm value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.algorithm_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.ModifyView.Builder clearAlgorithm() {
            this.bitField0_ &= -5;
            this.algorithm_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasSecurity() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public MysqlxCrud.ViewSqlSecurity getSecurity() {
            MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
            return result == null ? MysqlxCrud.ViewSqlSecurity.INVOKER : result;
         }

         public MysqlxCrud.ModifyView.Builder setSecurity(MysqlxCrud.ViewSqlSecurity value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.security_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.ModifyView.Builder clearSecurity() {
            this.bitField0_ &= -9;
            this.security_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasCheck() {
            return (this.bitField0_ & 16) != 0;
         }

         @Override
         public MysqlxCrud.ViewCheckOption getCheck() {
            MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
            return result == null ? MysqlxCrud.ViewCheckOption.LOCAL : result;
         }

         public MysqlxCrud.ModifyView.Builder setCheck(MysqlxCrud.ViewCheckOption value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 16;
               this.check_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.ModifyView.Builder clearCheck() {
            this.bitField0_ &= -17;
            this.check_ = 1;
            this.onChanged();
            return this;
         }

         private void ensureColumnIsMutable() {
            if ((this.bitField0_ & 32) == 0) {
               this.column_ = new LazyStringArrayList(this.column_);
               this.bitField0_ |= 32;
            }
         }

         public ProtocolStringList getColumnList() {
            return this.column_.getUnmodifiableView();
         }

         @Override
         public int getColumnCount() {
            return this.column_.size();
         }

         @Override
         public String getColumn(int index) {
            return this.column_.get(index);
         }

         @Override
         public ByteString getColumnBytes(int index) {
            return this.column_.getByteString(index);
         }

         public MysqlxCrud.ModifyView.Builder setColumn(int index, String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.set(index, value);
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.ModifyView.Builder addColumn(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.add(value);
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.ModifyView.Builder addAllColumn(Iterable<String> values) {
            this.ensureColumnIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.column_);
            this.onChanged();
            return this;
         }

         public MysqlxCrud.ModifyView.Builder clearColumn() {
            this.column_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= -33;
            this.onChanged();
            return this;
         }

         public MysqlxCrud.ModifyView.Builder addColumnBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.add(value);
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasStmt() {
            return (this.bitField0_ & 64) != 0;
         }

         @Override
         public MysqlxCrud.Find getStmt() {
            if (this.stmtBuilder_ == null) {
               return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
            } else {
               return this.stmtBuilder_.getMessage();
            }
         }

         public MysqlxCrud.ModifyView.Builder setStmt(MysqlxCrud.Find value) {
            if (this.stmtBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.stmt_ = value;
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public MysqlxCrud.ModifyView.Builder setStmt(MysqlxCrud.Find.Builder builderForValue) {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = builderForValue.build();
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 64;
            return this;
         }

         public MysqlxCrud.ModifyView.Builder mergeStmt(MysqlxCrud.Find value) {
            if (this.stmtBuilder_ == null) {
               if ((this.bitField0_ & 64) != 0 && this.stmt_ != null && this.stmt_ != MysqlxCrud.Find.getDefaultInstance()) {
                  this.stmt_ = MysqlxCrud.Find.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
               } else {
                  this.stmt_ = value;
               }

               this.onChanged();
            } else {
               this.stmtBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public MysqlxCrud.ModifyView.Builder clearStmt() {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
               this.onChanged();
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -65;
            return this;
         }

         public MysqlxCrud.Find.Builder getStmtBuilder() {
            this.bitField0_ |= 64;
            this.onChanged();
            return this.getStmtFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.FindOrBuilder getStmtOrBuilder() {
            if (this.stmtBuilder_ != null) {
               return this.stmtBuilder_.getMessageOrBuilder();
            } else {
               return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> getStmtFieldBuilder() {
            if (this.stmtBuilder_ == null) {
               this.stmtBuilder_ = new SingleFieldBuilderV3<>(this.getStmt(), this.getParentForChildren(), this.isClean());
               this.stmt_ = null;
            }

            return this.stmtBuilder_;
         }

         public final MysqlxCrud.ModifyView.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.ModifyView.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.ModifyView.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.ModifyView.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface ModifyViewOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      MysqlxCrud.Collection getCollection();

      MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDefiner();

      String getDefiner();

      ByteString getDefinerBytes();

      boolean hasAlgorithm();

      MysqlxCrud.ViewAlgorithm getAlgorithm();

      boolean hasSecurity();

      MysqlxCrud.ViewSqlSecurity getSecurity();

      boolean hasCheck();

      MysqlxCrud.ViewCheckOption getCheck();

      List<String> getColumnList();

      int getColumnCount();

      String getColumn(int var1);

      ByteString getColumnBytes(int var1);

      boolean hasStmt();

      MysqlxCrud.Find getStmt();

      MysqlxCrud.FindOrBuilder getStmtOrBuilder();
   }

   public static final class Order extends GeneratedMessageV3 implements MysqlxCrud.OrderOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int EXPR_FIELD_NUMBER = 1;
      private MysqlxExpr.Expr expr_;
      public static final int DIRECTION_FIELD_NUMBER = 2;
      private int direction_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.Order DEFAULT_INSTANCE = new MysqlxCrud.Order();
      @Deprecated
      public static final Parser<MysqlxCrud.Order> PARSER = new AbstractParser<MysqlxCrud.Order>() {
         public MysqlxCrud.Order parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.Order(input, extensionRegistry);
         }
      };

      private Order(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Order() {
         this.direction_ = 1;
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.Order();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Order(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxExpr.Expr.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.expr_.toBuilder();
                        }

                        this.expr_ = input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.expr_);
                           this.expr_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        int rawValue = input.readEnum();
                        MysqlxCrud.Order.Direction value = MysqlxCrud.Order.Direction.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(2, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.direction_ = rawValue;
                        }
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw new InvalidProtocolBufferException(var14).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Order_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.Order.class, MysqlxCrud.Order.Builder.class);
      }

      @Override
      public boolean hasExpr() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxExpr.Expr getExpr() {
         return this.expr_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getExprOrBuilder() {
         return this.expr_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;
      }

      @Override
      public boolean hasDirection() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public MysqlxCrud.Order.Direction getDirection() {
         MysqlxCrud.Order.Direction result = MysqlxCrud.Order.Direction.valueOf(this.direction_);
         return result == null ? MysqlxCrud.Order.Direction.ASC : result;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasExpr()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getExpr().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getExpr());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.direction_);
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getExpr());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.direction_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.Order)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.Order other = (MysqlxCrud.Order)obj;
            if (this.hasExpr() != other.hasExpr()) {
               return false;
            } else if (this.hasExpr() && !this.getExpr().equals(other.getExpr())) {
               return false;
            } else if (this.hasDirection() != other.hasDirection()) {
               return false;
            } else {
               return this.hasDirection() && this.direction_ != other.direction_ ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasExpr()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getExpr().hashCode();
            }

            if (this.hasDirection()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.direction_;
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.Order parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Order parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Order parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Order parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Order parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Order parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Order parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Order parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Order parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Order parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Order parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Order parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.Order.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.Order.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.Order.Builder newBuilder(MysqlxCrud.Order prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.Order.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.Order.Builder() : new MysqlxCrud.Order.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.Order.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.Order.Builder(parent);
      }

      public static MysqlxCrud.Order getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.Order> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.Order> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.Order getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.Order.Builder> implements MysqlxCrud.OrderOrBuilder {
         private int bitField0_;
         private MysqlxExpr.Expr expr_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> exprBuilder_;
         private int direction_ = 1;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Order_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.Order.class, MysqlxCrud.Order.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Order.alwaysUseFieldBuilders) {
               this.getExprFieldBuilder();
            }
         }

         public MysqlxCrud.Order.Builder clear() {
            super.clear();
            if (this.exprBuilder_ == null) {
               this.expr_ = null;
            } else {
               this.exprBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.direction_ = 1;
            this.bitField0_ &= -3;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor;
         }

         public MysqlxCrud.Order getDefaultInstanceForType() {
            return MysqlxCrud.Order.getDefaultInstance();
         }

         public MysqlxCrud.Order build() {
            MysqlxCrud.Order result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.Order buildPartial() {
            MysqlxCrud.Order result = new MysqlxCrud.Order(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.exprBuilder_ == null) {
                  result.expr_ = this.expr_;
               } else {
                  result.expr_ = this.exprBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.direction_ = this.direction_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.Order.Builder clone() {
            return (MysqlxCrud.Order.Builder)super.clone();
         }

         public MysqlxCrud.Order.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Order.Builder)super.setField(field, value);
         }

         public MysqlxCrud.Order.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.Order.Builder)super.clearField(field);
         }

         public MysqlxCrud.Order.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.Order.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.Order.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.Order.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.Order.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Order.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.Order.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.Order) {
               return this.mergeFrom((MysqlxCrud.Order)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.Order.Builder mergeFrom(MysqlxCrud.Order other) {
            if (other == MysqlxCrud.Order.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasExpr()) {
                  this.mergeExpr(other.getExpr());
               }

               if (other.hasDirection()) {
                  this.setDirection(other.getDirection());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return !this.hasExpr() ? false : this.getExpr().isInitialized();
         }

         public MysqlxCrud.Order.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.Order parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.Order.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.Order)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasExpr() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxExpr.Expr getExpr() {
            if (this.exprBuilder_ == null) {
               return this.expr_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;
            } else {
               return this.exprBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Order.Builder setExpr(MysqlxExpr.Expr value) {
            if (this.exprBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.expr_ = value;
               this.onChanged();
            } else {
               this.exprBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Order.Builder setExpr(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.exprBuilder_ == null) {
               this.expr_ = builderForValue.build();
               this.onChanged();
            } else {
               this.exprBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Order.Builder mergeExpr(MysqlxExpr.Expr value) {
            if (this.exprBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.expr_ != null && this.expr_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.expr_ = MysqlxExpr.Expr.newBuilder(this.expr_).mergeFrom(value).buildPartial();
               } else {
                  this.expr_ = value;
               }

               this.onChanged();
            } else {
               this.exprBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Order.Builder clearExpr() {
            if (this.exprBuilder_ == null) {
               this.expr_ = null;
               this.onChanged();
            } else {
               this.exprBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxExpr.Expr.Builder getExprBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getExprFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getExprOrBuilder() {
            if (this.exprBuilder_ != null) {
               return this.exprBuilder_.getMessageOrBuilder();
            } else {
               return this.expr_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getExprFieldBuilder() {
            if (this.exprBuilder_ == null) {
               this.exprBuilder_ = new SingleFieldBuilderV3<>(this.getExpr(), this.getParentForChildren(), this.isClean());
               this.expr_ = null;
            }

            return this.exprBuilder_;
         }

         @Override
         public boolean hasDirection() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxCrud.Order.Direction getDirection() {
            MysqlxCrud.Order.Direction result = MysqlxCrud.Order.Direction.valueOf(this.direction_);
            return result == null ? MysqlxCrud.Order.Direction.ASC : result;
         }

         public MysqlxCrud.Order.Builder setDirection(MysqlxCrud.Order.Direction value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.direction_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Order.Builder clearDirection() {
            this.bitField0_ &= -3;
            this.direction_ = 1;
            this.onChanged();
            return this;
         }

         public final MysqlxCrud.Order.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Order.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.Order.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Order.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static enum Direction implements ProtocolMessageEnum {
         ASC(1),
         DESC(2);

         public static final int ASC_VALUE = 1;
         public static final int DESC_VALUE = 2;
         private static final Internal.EnumLiteMap<MysqlxCrud.Order.Direction> internalValueMap = new Internal.EnumLiteMap<MysqlxCrud.Order.Direction>() {
            public MysqlxCrud.Order.Direction findValueByNumber(int number) {
               return MysqlxCrud.Order.Direction.forNumber(number);
            }
         };
         private static final MysqlxCrud.Order.Direction[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxCrud.Order.Direction valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxCrud.Order.Direction forNumber(int value) {
            switch (value) {
               case 1:
                  return ASC;
               case 2:
                  return DESC;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<MysqlxCrud.Order.Direction> internalGetValueMap() {
            return internalValueMap;
         }

         @Override
         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return getDescriptor().getValues().get(this.ordinal());
         }

         @Override
         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return MysqlxCrud.Order.getDescriptor().getEnumTypes().get(0);
         }

         public static MysqlxCrud.Order.Direction valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Direction(int value) {
            this.value = value;
         }
      }
   }

   public interface OrderOrBuilder extends MessageOrBuilder {
      boolean hasExpr();

      MysqlxExpr.Expr getExpr();

      MysqlxExpr.ExprOrBuilder getExprOrBuilder();

      boolean hasDirection();

      MysqlxCrud.Order.Direction getDirection();
   }

   public static final class Projection extends GeneratedMessageV3 implements MysqlxCrud.ProjectionOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int SOURCE_FIELD_NUMBER = 1;
      private MysqlxExpr.Expr source_;
      public static final int ALIAS_FIELD_NUMBER = 2;
      private volatile Object alias_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.Projection DEFAULT_INSTANCE = new MysqlxCrud.Projection();
      @Deprecated
      public static final Parser<MysqlxCrud.Projection> PARSER = new AbstractParser<MysqlxCrud.Projection>() {
         public MysqlxCrud.Projection parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.Projection(input, extensionRegistry);
         }
      };

      private Projection(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Projection() {
         this.alias_ = "";
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.Projection();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Projection(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxExpr.Expr.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.source_.toBuilder();
                        }

                        this.source_ = input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.source_);
                           this.source_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.alias_ = bs;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.Projection.class, MysqlxCrud.Projection.Builder.class);
      }

      @Override
      public boolean hasSource() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxExpr.Expr getSource() {
         return this.source_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getSourceOrBuilder() {
         return this.source_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;
      }

      @Override
      public boolean hasAlias() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getAlias() {
         Object ref = this.alias_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.alias_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getAliasBytes() {
         Object ref = this.alias_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.alias_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasSource()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getSource().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getSource());
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.alias_);
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getSource());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.alias_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.Projection)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.Projection other = (MysqlxCrud.Projection)obj;
            if (this.hasSource() != other.hasSource()) {
               return false;
            } else if (this.hasSource() && !this.getSource().equals(other.getSource())) {
               return false;
            } else if (this.hasAlias() != other.hasAlias()) {
               return false;
            } else {
               return this.hasAlias() && !this.getAlias().equals(other.getAlias()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasSource()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getSource().hashCode();
            }

            if (this.hasAlias()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getAlias().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.Projection parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Projection parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Projection parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Projection parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Projection parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Projection parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Projection parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Projection parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Projection parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Projection parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Projection parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Projection parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.Projection.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.Projection.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.Projection.Builder newBuilder(MysqlxCrud.Projection prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.Projection.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.Projection.Builder() : new MysqlxCrud.Projection.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.Projection.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.Projection.Builder(parent);
      }

      public static MysqlxCrud.Projection getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.Projection> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.Projection> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.Projection getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.Projection.Builder> implements MysqlxCrud.ProjectionOrBuilder {
         private int bitField0_;
         private MysqlxExpr.Expr source_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> sourceBuilder_;
         private Object alias_ = "";

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.Projection.class, MysqlxCrud.Projection.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Projection.alwaysUseFieldBuilders) {
               this.getSourceFieldBuilder();
            }
         }

         public MysqlxCrud.Projection.Builder clear() {
            super.clear();
            if (this.sourceBuilder_ == null) {
               this.source_ = null;
            } else {
               this.sourceBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.alias_ = "";
            this.bitField0_ &= -3;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor;
         }

         public MysqlxCrud.Projection getDefaultInstanceForType() {
            return MysqlxCrud.Projection.getDefaultInstance();
         }

         public MysqlxCrud.Projection build() {
            MysqlxCrud.Projection result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.Projection buildPartial() {
            MysqlxCrud.Projection result = new MysqlxCrud.Projection(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.sourceBuilder_ == null) {
                  result.source_ = this.source_;
               } else {
                  result.source_ = this.sourceBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.alias_ = this.alias_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.Projection.Builder clone() {
            return (MysqlxCrud.Projection.Builder)super.clone();
         }

         public MysqlxCrud.Projection.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Projection.Builder)super.setField(field, value);
         }

         public MysqlxCrud.Projection.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.Projection.Builder)super.clearField(field);
         }

         public MysqlxCrud.Projection.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.Projection.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.Projection.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.Projection.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.Projection.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Projection.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.Projection.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.Projection) {
               return this.mergeFrom((MysqlxCrud.Projection)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.Projection.Builder mergeFrom(MysqlxCrud.Projection other) {
            if (other == MysqlxCrud.Projection.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasSource()) {
                  this.mergeSource(other.getSource());
               }

               if (other.hasAlias()) {
                  this.bitField0_ |= 2;
                  this.alias_ = other.alias_;
                  this.onChanged();
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return !this.hasSource() ? false : this.getSource().isInitialized();
         }

         public MysqlxCrud.Projection.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.Projection parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.Projection.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.Projection)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasSource() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxExpr.Expr getSource() {
            if (this.sourceBuilder_ == null) {
               return this.source_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;
            } else {
               return this.sourceBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Projection.Builder setSource(MysqlxExpr.Expr value) {
            if (this.sourceBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.source_ = value;
               this.onChanged();
            } else {
               this.sourceBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Projection.Builder setSource(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.sourceBuilder_ == null) {
               this.source_ = builderForValue.build();
               this.onChanged();
            } else {
               this.sourceBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Projection.Builder mergeSource(MysqlxExpr.Expr value) {
            if (this.sourceBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.source_ != null && this.source_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.source_ = MysqlxExpr.Expr.newBuilder(this.source_).mergeFrom(value).buildPartial();
               } else {
                  this.source_ = value;
               }

               this.onChanged();
            } else {
               this.sourceBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Projection.Builder clearSource() {
            if (this.sourceBuilder_ == null) {
               this.source_ = null;
               this.onChanged();
            } else {
               this.sourceBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxExpr.Expr.Builder getSourceBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getSourceFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getSourceOrBuilder() {
            if (this.sourceBuilder_ != null) {
               return this.sourceBuilder_.getMessageOrBuilder();
            } else {
               return this.source_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getSourceFieldBuilder() {
            if (this.sourceBuilder_ == null) {
               this.sourceBuilder_ = new SingleFieldBuilderV3<>(this.getSource(), this.getParentForChildren(), this.isClean());
               this.source_ = null;
            }

            return this.sourceBuilder_;
         }

         @Override
         public boolean hasAlias() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getAlias() {
            Object ref = this.alias_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.alias_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getAliasBytes() {
            Object ref = this.alias_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.alias_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxCrud.Projection.Builder setAlias(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.alias_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Projection.Builder clearAlias() {
            this.bitField0_ &= -3;
            this.alias_ = MysqlxCrud.Projection.getDefaultInstance().getAlias();
            this.onChanged();
            return this;
         }

         public MysqlxCrud.Projection.Builder setAliasBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.alias_ = value;
               this.onChanged();
               return this;
            }
         }

         public final MysqlxCrud.Projection.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Projection.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.Projection.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Projection.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface ProjectionOrBuilder extends MessageOrBuilder {
      boolean hasSource();

      MysqlxExpr.Expr getSource();

      MysqlxExpr.ExprOrBuilder getSourceOrBuilder();

      boolean hasAlias();

      String getAlias();

      ByteString getAliasBytes();
   }

   public static final class Update extends GeneratedMessageV3 implements MysqlxCrud.UpdateOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 2;
      private MysqlxCrud.Collection collection_;
      public static final int DATA_MODEL_FIELD_NUMBER = 3;
      private int dataModel_;
      public static final int CRITERIA_FIELD_NUMBER = 4;
      private MysqlxExpr.Expr criteria_;
      public static final int LIMIT_FIELD_NUMBER = 5;
      private MysqlxCrud.Limit limit_;
      public static final int ORDER_FIELD_NUMBER = 6;
      private List<MysqlxCrud.Order> order_;
      public static final int OPERATION_FIELD_NUMBER = 7;
      private List<MysqlxCrud.UpdateOperation> operation_;
      public static final int ARGS_FIELD_NUMBER = 8;
      private List<MysqlxDatatypes.Scalar> args_;
      public static final int LIMIT_EXPR_FIELD_NUMBER = 9;
      private MysqlxCrud.LimitExpr limitExpr_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.Update DEFAULT_INSTANCE = new MysqlxCrud.Update();
      @Deprecated
      public static final Parser<MysqlxCrud.Update> PARSER = new AbstractParser<MysqlxCrud.Update>() {
         public MysqlxCrud.Update parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.Update(input, extensionRegistry);
         }
      };

      private Update(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Update() {
         this.dataModel_ = 1;
         this.order_ = Collections.emptyList();
         this.operation_ = Collections.emptyList();
         this.args_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.Update();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Update(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 18:
                        MysqlxCrud.Collection.Builder subBuilderxxx = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilderxxx = this.collection_.toBuilder();
                        }

                        this.collection_ = input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilderxxx != null) {
                           subBuilderxxx.mergeFrom(this.collection_);
                           this.collection_ = subBuilderxxx.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 24:
                        int rawValue = input.readEnum();
                        MysqlxCrud.DataModel value = MysqlxCrud.DataModel.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(3, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.dataModel_ = rawValue;
                        }
                        break;
                     case 34:
                        MysqlxExpr.Expr.Builder subBuilderxx = null;
                        if ((this.bitField0_ & 4) != 0) {
                           subBuilderxx = this.criteria_.toBuilder();
                        }

                        this.criteria_ = input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilderxx != null) {
                           subBuilderxx.mergeFrom(this.criteria_);
                           this.criteria_ = subBuilderxx.buildPartial();
                        }

                        this.bitField0_ |= 4;
                        break;
                     case 42:
                        MysqlxCrud.Limit.Builder subBuilderx = null;
                        if ((this.bitField0_ & 8) != 0) {
                           subBuilderx = this.limit_.toBuilder();
                        }

                        this.limit_ = input.readMessage(MysqlxCrud.Limit.PARSER, extensionRegistry);
                        if (subBuilderx != null) {
                           subBuilderx.mergeFrom(this.limit_);
                           this.limit_ = subBuilderx.buildPartial();
                        }

                        this.bitField0_ |= 8;
                        break;
                     case 50:
                        if ((mutable_bitField0_ & 16) == 0) {
                           this.order_ = new ArrayList<>();
                           mutable_bitField0_ |= 16;
                        }

                        this.order_.add(input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry));
                        break;
                     case 58:
                        if ((mutable_bitField0_ & 32) == 0) {
                           this.operation_ = new ArrayList<>();
                           mutable_bitField0_ |= 32;
                        }

                        this.operation_.add(input.readMessage(MysqlxCrud.UpdateOperation.PARSER, extensionRegistry));
                        break;
                     case 66:
                        if ((mutable_bitField0_ & 64) == 0) {
                           this.args_ = new ArrayList<>();
                           mutable_bitField0_ |= 64;
                        }

                        this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry));
                        break;
                     case 74:
                        MysqlxCrud.LimitExpr.Builder subBuilder = null;
                        if ((this.bitField0_ & 16) != 0) {
                           subBuilder = this.limitExpr_.toBuilder();
                        }

                        this.limitExpr_ = input.readMessage(MysqlxCrud.LimitExpr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.limitExpr_);
                           this.limitExpr_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 16;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw new InvalidProtocolBufferException(var14).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 16) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
               }

               if ((mutable_bitField0_ & 32) != 0) {
                  this.operation_ = Collections.unmodifiableList(this.operation_);
               }

               if ((mutable_bitField0_ & 64) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Update_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.Update.class, MysqlxCrud.Update.Builder.class);
      }

      @Override
      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxCrud.Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      @Override
      public boolean hasDataModel() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public MysqlxCrud.DataModel getDataModel() {
         MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
         return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
      }

      @Override
      public boolean hasCriteria() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public MysqlxExpr.Expr getCriteria() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      @Override
      public boolean hasLimit() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public MysqlxCrud.Limit getLimit() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      @Override
      public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      @Override
      public List<MysqlxCrud.Order> getOrderList() {
         return this.order_;
      }

      @Override
      public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
         return this.order_;
      }

      @Override
      public int getOrderCount() {
         return this.order_.size();
      }

      @Override
      public MysqlxCrud.Order getOrder(int index) {
         return this.order_.get(index);
      }

      @Override
      public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
         return this.order_.get(index);
      }

      @Override
      public List<MysqlxCrud.UpdateOperation> getOperationList() {
         return this.operation_;
      }

      @Override
      public List<? extends MysqlxCrud.UpdateOperationOrBuilder> getOperationOrBuilderList() {
         return this.operation_;
      }

      @Override
      public int getOperationCount() {
         return this.operation_.size();
      }

      @Override
      public MysqlxCrud.UpdateOperation getOperation(int index) {
         return this.operation_.get(index);
      }

      @Override
      public MysqlxCrud.UpdateOperationOrBuilder getOperationOrBuilder(int index) {
         return this.operation_.get(index);
      }

      @Override
      public List<MysqlxDatatypes.Scalar> getArgsList() {
         return this.args_;
      }

      @Override
      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
         return this.args_;
      }

      @Override
      public int getArgsCount() {
         return this.args_.size();
      }

      @Override
      public MysqlxDatatypes.Scalar getArgs(int index) {
         return this.args_.get(index);
      }

      @Override
      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
         return this.args_.get(index);
      }

      @Override
      public boolean hasLimitExpr() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public MysqlxCrud.LimitExpr getLimitExpr() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      @Override
      public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            for (int i = 0; i < this.getOrderCount(); i++) {
               if (!this.getOrder(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for (int ix = 0; ix < this.getOperationCount(); ix++) {
               if (!this.getOperation(ix).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for (int ixx = 0; ixx < this.getArgsCount(); ixx++) {
               if (!this.getArgs(ixx).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            if (this.hasLimitExpr() && !this.getLimitExpr().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else {
               this.memoizedIsInitialized = 1;
               return true;
            }
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(2, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(3, this.dataModel_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeMessage(4, this.getCriteria());
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeMessage(5, this.getLimit());
         }

         for (int i = 0; i < this.order_.size(); i++) {
            output.writeMessage(6, this.order_.get(i));
         }

         for (int i = 0; i < this.operation_.size(); i++) {
            output.writeMessage(7, this.operation_.get(i));
         }

         for (int i = 0; i < this.args_.size(); i++) {
            output.writeMessage(8, this.args_.get(i));
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeMessage(9, this.getLimitExpr());
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(2, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(3, this.dataModel_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeMessageSize(4, this.getCriteria());
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeMessageSize(5, this.getLimit());
            }

            for (int i = 0; i < this.order_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(6, this.order_.get(i));
            }

            for (int i = 0; i < this.operation_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(7, this.operation_.get(i));
            }

            for (int i = 0; i < this.args_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(8, this.args_.get(i));
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeMessageSize(9, this.getLimitExpr());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.Update)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.Update other = (MysqlxCrud.Update)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDataModel() != other.hasDataModel()) {
               return false;
            } else if (this.hasDataModel() && this.dataModel_ != other.dataModel_) {
               return false;
            } else if (this.hasCriteria() != other.hasCriteria()) {
               return false;
            } else if (this.hasCriteria() && !this.getCriteria().equals(other.getCriteria())) {
               return false;
            } else if (this.hasLimit() != other.hasLimit()) {
               return false;
            } else if (this.hasLimit() && !this.getLimit().equals(other.getLimit())) {
               return false;
            } else if (!this.getOrderList().equals(other.getOrderList())) {
               return false;
            } else if (!this.getOperationList().equals(other.getOperationList())) {
               return false;
            } else if (!this.getArgsList().equals(other.getArgsList())) {
               return false;
            } else if (this.hasLimitExpr() != other.hasLimitExpr()) {
               return false;
            } else {
               return this.hasLimitExpr() && !this.getLimitExpr().equals(other.getLimitExpr()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDataModel()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.dataModel_;
            }

            if (this.hasCriteria()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getCriteria().hashCode();
            }

            if (this.hasLimit()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getLimit().hashCode();
            }

            if (this.getOrderCount() > 0) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getOrderList().hashCode();
            }

            if (this.getOperationCount() > 0) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getOperationList().hashCode();
            }

            if (this.getArgsCount() > 0) {
               hash = 37 * hash + 8;
               hash = 53 * hash + this.getArgsList().hashCode();
            }

            if (this.hasLimitExpr()) {
               hash = 37 * hash + 9;
               hash = 53 * hash + this.getLimitExpr().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.Update parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Update parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Update parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Update parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Update parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.Update parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.Update parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Update parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Update parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Update parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.Update parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.Update parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.Update.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.Update.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.Update.Builder newBuilder(MysqlxCrud.Update prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.Update.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.Update.Builder() : new MysqlxCrud.Update.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.Update.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.Update.Builder(parent);
      }

      public static MysqlxCrud.Update getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.Update> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.Update> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.Update getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.Update.Builder> implements MysqlxCrud.UpdateOrBuilder {
         private int bitField0_;
         private MysqlxCrud.Collection collection_;
         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
         private int dataModel_ = 1;
         private MysqlxExpr.Expr criteria_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
         private MysqlxCrud.Limit limit_;
         private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> limitBuilder_;
         private List<MysqlxCrud.Order> order_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> orderBuilder_;
         private List<MysqlxCrud.UpdateOperation> operation_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxCrud.UpdateOperation, MysqlxCrud.UpdateOperation.Builder, MysqlxCrud.UpdateOperationOrBuilder> operationBuilder_;
         private List<MysqlxDatatypes.Scalar> args_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
         private MysqlxCrud.LimitExpr limitExpr_;
         private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> limitExprBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Update_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.Update.class, MysqlxCrud.Update.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Update.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getCriteriaFieldBuilder();
               this.getLimitFieldBuilder();
               this.getOrderFieldBuilder();
               this.getOperationFieldBuilder();
               this.getArgsFieldBuilder();
               this.getLimitExprFieldBuilder();
            }
         }

         public MysqlxCrud.Update.Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.dataModel_ = 1;
            this.bitField0_ &= -3;
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -5;
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -9;
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -17;
            } else {
               this.orderBuilder_.clear();
            }

            if (this.operationBuilder_ == null) {
               this.operation_ = Collections.emptyList();
               this.bitField0_ &= -33;
            } else {
               this.operationBuilder_.clear();
            }

            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -65;
            } else {
               this.argsBuilder_.clear();
            }

            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -129;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor;
         }

         public MysqlxCrud.Update getDefaultInstanceForType() {
            return MysqlxCrud.Update.getDefaultInstance();
         }

         public MysqlxCrud.Update build() {
            MysqlxCrud.Update result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.Update buildPartial() {
            MysqlxCrud.Update result = new MysqlxCrud.Update(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.dataModel_ = this.dataModel_;
            if ((from_bitField0_ & 4) != 0) {
               if (this.criteriaBuilder_ == null) {
                  result.criteria_ = this.criteria_;
               } else {
                  result.criteria_ = this.criteriaBuilder_.build();
               }

               to_bitField0_ |= 4;
            }

            if ((from_bitField0_ & 8) != 0) {
               if (this.limitBuilder_ == null) {
                  result.limit_ = this.limit_;
               } else {
                  result.limit_ = this.limitBuilder_.build();
               }

               to_bitField0_ |= 8;
            }

            if (this.orderBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
                  this.bitField0_ &= -17;
               }

               result.order_ = this.order_;
            } else {
               result.order_ = this.orderBuilder_.build();
            }

            if (this.operationBuilder_ == null) {
               if ((this.bitField0_ & 32) != 0) {
                  this.operation_ = Collections.unmodifiableList(this.operation_);
                  this.bitField0_ &= -33;
               }

               result.operation_ = this.operation_;
            } else {
               result.operation_ = this.operationBuilder_.build();
            }

            if (this.argsBuilder_ == null) {
               if ((this.bitField0_ & 64) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
                  this.bitField0_ &= -65;
               }

               result.args_ = this.args_;
            } else {
               result.args_ = this.argsBuilder_.build();
            }

            if ((from_bitField0_ & 128) != 0) {
               if (this.limitExprBuilder_ == null) {
                  result.limitExpr_ = this.limitExpr_;
               } else {
                  result.limitExpr_ = this.limitExprBuilder_.build();
               }

               to_bitField0_ |= 16;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.Update.Builder clone() {
            return (MysqlxCrud.Update.Builder)super.clone();
         }

         public MysqlxCrud.Update.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Update.Builder)super.setField(field, value);
         }

         public MysqlxCrud.Update.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.Update.Builder)super.clearField(field);
         }

         public MysqlxCrud.Update.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.Update.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.Update.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.Update.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.Update.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.Update.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.Update.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.Update) {
               return this.mergeFrom((MysqlxCrud.Update)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.Update.Builder mergeFrom(MysqlxCrud.Update other) {
            if (other == MysqlxCrud.Update.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDataModel()) {
                  this.setDataModel(other.getDataModel());
               }

               if (other.hasCriteria()) {
                  this.mergeCriteria(other.getCriteria());
               }

               if (other.hasLimit()) {
                  this.mergeLimit(other.getLimit());
               }

               if (this.orderBuilder_ == null) {
                  if (!other.order_.isEmpty()) {
                     if (this.order_.isEmpty()) {
                        this.order_ = other.order_;
                        this.bitField0_ &= -17;
                     } else {
                        this.ensureOrderIsMutable();
                        this.order_.addAll(other.order_);
                     }

                     this.onChanged();
                  }
               } else if (!other.order_.isEmpty()) {
                  if (this.orderBuilder_.isEmpty()) {
                     this.orderBuilder_.dispose();
                     this.orderBuilder_ = null;
                     this.order_ = other.order_;
                     this.bitField0_ &= -17;
                     this.orderBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? this.getOrderFieldBuilder() : null;
                  } else {
                     this.orderBuilder_.addAllMessages(other.order_);
                  }
               }

               if (this.operationBuilder_ == null) {
                  if (!other.operation_.isEmpty()) {
                     if (this.operation_.isEmpty()) {
                        this.operation_ = other.operation_;
                        this.bitField0_ &= -33;
                     } else {
                        this.ensureOperationIsMutable();
                        this.operation_.addAll(other.operation_);
                     }

                     this.onChanged();
                  }
               } else if (!other.operation_.isEmpty()) {
                  if (this.operationBuilder_.isEmpty()) {
                     this.operationBuilder_.dispose();
                     this.operationBuilder_ = null;
                     this.operation_ = other.operation_;
                     this.bitField0_ &= -33;
                     this.operationBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? this.getOperationFieldBuilder() : null;
                  } else {
                     this.operationBuilder_.addAllMessages(other.operation_);
                  }
               }

               if (this.argsBuilder_ == null) {
                  if (!other.args_.isEmpty()) {
                     if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= -65;
                     } else {
                        this.ensureArgsIsMutable();
                        this.args_.addAll(other.args_);
                     }

                     this.onChanged();
                  }
               } else if (!other.args_.isEmpty()) {
                  if (this.argsBuilder_.isEmpty()) {
                     this.argsBuilder_.dispose();
                     this.argsBuilder_ = null;
                     this.args_ = other.args_;
                     this.bitField0_ &= -65;
                     this.argsBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? this.getArgsFieldBuilder() : null;
                  } else {
                     this.argsBuilder_.addAllMessages(other.args_);
                  }
               }

               if (other.hasLimitExpr()) {
                  this.mergeLimitExpr(other.getLimitExpr());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else if (!this.getCollection().isInitialized()) {
               return false;
            } else if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
               return false;
            } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
               return false;
            } else {
               for (int i = 0; i < this.getOrderCount(); i++) {
                  if (!this.getOrder(i).isInitialized()) {
                     return false;
                  }
               }

               for (int ix = 0; ix < this.getOperationCount(); ix++) {
                  if (!this.getOperation(ix).isInitialized()) {
                     return false;
                  }
               }

               for (int ixx = 0; ixx < this.getArgsCount(); ixx++) {
                  if (!this.getArgs(ixx).isInitialized()) {
                     return false;
                  }
               }

               return !this.hasLimitExpr() || this.getLimitExpr().isInitialized();
            }
         }

         public MysqlxCrud.Update.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.Update parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.Update.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.Update)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxCrud.Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return this.collectionBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Update.Builder setCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Update.Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Update.Builder mergeCollection(MysqlxCrud.Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.Update.Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxCrud.Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getCollectionFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3<>(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         @Override
         public boolean hasDataModel() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxCrud.DataModel getDataModel() {
            MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
            return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
         }

         public MysqlxCrud.Update.Builder setDataModel(MysqlxCrud.DataModel value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.dataModel_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.Update.Builder clearDataModel() {
            this.bitField0_ &= -3;
            this.dataModel_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasCriteria() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public MysqlxExpr.Expr getCriteria() {
            if (this.criteriaBuilder_ == null) {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            } else {
               return this.criteriaBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Update.Builder setCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.criteria_ = value;
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public MysqlxCrud.Update.Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = builderForValue.build();
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 4;
            return this;
         }

         public MysqlxCrud.Update.Builder mergeCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial();
               } else {
                  this.criteria_ = value;
               }

               this.onChanged();
            } else {
               this.criteriaBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public MysqlxCrud.Update.Builder clearCriteria() {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
               this.onChanged();
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -5;
            return this;
         }

         public MysqlxExpr.Expr.Builder getCriteriaBuilder() {
            this.bitField0_ |= 4;
            this.onChanged();
            return this.getCriteriaFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
            if (this.criteriaBuilder_ != null) {
               return this.criteriaBuilder_.getMessageOrBuilder();
            } else {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() {
            if (this.criteriaBuilder_ == null) {
               this.criteriaBuilder_ = new SingleFieldBuilderV3<>(this.getCriteria(), this.getParentForChildren(), this.isClean());
               this.criteria_ = null;
            }

            return this.criteriaBuilder_;
         }

         @Override
         public boolean hasLimit() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public MysqlxCrud.Limit getLimit() {
            if (this.limitBuilder_ == null) {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            } else {
               return this.limitBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Update.Builder setLimit(MysqlxCrud.Limit value) {
            if (this.limitBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limit_ = value;
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public MysqlxCrud.Update.Builder setLimit(MysqlxCrud.Limit.Builder builderForValue) {
            if (this.limitBuilder_ == null) {
               this.limit_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 8;
            return this;
         }

         public MysqlxCrud.Update.Builder mergeLimit(MysqlxCrud.Limit value) {
            if (this.limitBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) {
                  this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial();
               } else {
                  this.limit_ = value;
               }

               this.onChanged();
            } else {
               this.limitBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public MysqlxCrud.Update.Builder clearLimit() {
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
               this.onChanged();
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -9;
            return this;
         }

         public MysqlxCrud.Limit.Builder getLimitBuilder() {
            this.bitField0_ |= 8;
            this.onChanged();
            return this.getLimitFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
            if (this.limitBuilder_ != null) {
               return this.limitBuilder_.getMessageOrBuilder();
            } else {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> getLimitFieldBuilder() {
            if (this.limitBuilder_ == null) {
               this.limitBuilder_ = new SingleFieldBuilderV3<>(this.getLimit(), this.getParentForChildren(), this.isClean());
               this.limit_ = null;
            }

            return this.limitBuilder_;
         }

         private void ensureOrderIsMutable() {
            if ((this.bitField0_ & 16) == 0) {
               this.order_ = new ArrayList<>(this.order_);
               this.bitField0_ |= 16;
            }
         }

         @Override
         public List<MysqlxCrud.Order> getOrderList() {
            return this.orderBuilder_ == null ? Collections.unmodifiableList(this.order_) : this.orderBuilder_.getMessageList();
         }

         @Override
         public int getOrderCount() {
            return this.orderBuilder_ == null ? this.order_.size() : this.orderBuilder_.getCount();
         }

         @Override
         public MysqlxCrud.Order getOrder(int index) {
            return this.orderBuilder_ == null ? this.order_.get(index) : this.orderBuilder_.getMessage(index);
         }

         public MysqlxCrud.Update.Builder setOrder(int index, MysqlxCrud.Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.set(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder setOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addOrder(MysqlxCrud.Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addOrder(int index, MysqlxCrud.Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addOrder(MysqlxCrud.Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addAllOrder(Iterable<? extends MysqlxCrud.Order> values) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.order_);
               this.onChanged();
            } else {
               this.orderBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder clearOrder() {
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -17;
               this.onChanged();
            } else {
               this.orderBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Update.Builder removeOrder(int index) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.remove(index);
               this.onChanged();
            } else {
               this.orderBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxCrud.Order.Builder getOrderBuilder(int index) {
            return this.getOrderFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
            return this.orderBuilder_ == null ? this.order_.get(index) : this.orderBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
            return this.orderBuilder_ != null ? this.orderBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.order_);
         }

         public MysqlxCrud.Order.Builder addOrderBuilder() {
            return this.getOrderFieldBuilder().addBuilder(MysqlxCrud.Order.getDefaultInstance());
         }

         public MysqlxCrud.Order.Builder addOrderBuilder(int index) {
            return this.getOrderFieldBuilder().addBuilder(index, MysqlxCrud.Order.getDefaultInstance());
         }

         public List<MysqlxCrud.Order.Builder> getOrderBuilderList() {
            return this.getOrderFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> getOrderFieldBuilder() {
            if (this.orderBuilder_ == null) {
               this.orderBuilder_ = new RepeatedFieldBuilderV3<>(this.order_, (this.bitField0_ & 16) != 0, this.getParentForChildren(), this.isClean());
               this.order_ = null;
            }

            return this.orderBuilder_;
         }

         private void ensureOperationIsMutable() {
            if ((this.bitField0_ & 32) == 0) {
               this.operation_ = new ArrayList<>(this.operation_);
               this.bitField0_ |= 32;
            }
         }

         @Override
         public List<MysqlxCrud.UpdateOperation> getOperationList() {
            return this.operationBuilder_ == null ? Collections.unmodifiableList(this.operation_) : this.operationBuilder_.getMessageList();
         }

         @Override
         public int getOperationCount() {
            return this.operationBuilder_ == null ? this.operation_.size() : this.operationBuilder_.getCount();
         }

         @Override
         public MysqlxCrud.UpdateOperation getOperation(int index) {
            return this.operationBuilder_ == null ? this.operation_.get(index) : this.operationBuilder_.getMessage(index);
         }

         public MysqlxCrud.Update.Builder setOperation(int index, MysqlxCrud.UpdateOperation value) {
            if (this.operationBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOperationIsMutable();
               this.operation_.set(index, value);
               this.onChanged();
            } else {
               this.operationBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder setOperation(int index, MysqlxCrud.UpdateOperation.Builder builderForValue) {
            if (this.operationBuilder_ == null) {
               this.ensureOperationIsMutable();
               this.operation_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.operationBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addOperation(MysqlxCrud.UpdateOperation value) {
            if (this.operationBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOperationIsMutable();
               this.operation_.add(value);
               this.onChanged();
            } else {
               this.operationBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addOperation(int index, MysqlxCrud.UpdateOperation value) {
            if (this.operationBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOperationIsMutable();
               this.operation_.add(index, value);
               this.onChanged();
            } else {
               this.operationBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addOperation(MysqlxCrud.UpdateOperation.Builder builderForValue) {
            if (this.operationBuilder_ == null) {
               this.ensureOperationIsMutable();
               this.operation_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.operationBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addOperation(int index, MysqlxCrud.UpdateOperation.Builder builderForValue) {
            if (this.operationBuilder_ == null) {
               this.ensureOperationIsMutable();
               this.operation_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.operationBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addAllOperation(Iterable<? extends MysqlxCrud.UpdateOperation> values) {
            if (this.operationBuilder_ == null) {
               this.ensureOperationIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.operation_);
               this.onChanged();
            } else {
               this.operationBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder clearOperation() {
            if (this.operationBuilder_ == null) {
               this.operation_ = Collections.emptyList();
               this.bitField0_ &= -33;
               this.onChanged();
            } else {
               this.operationBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Update.Builder removeOperation(int index) {
            if (this.operationBuilder_ == null) {
               this.ensureOperationIsMutable();
               this.operation_.remove(index);
               this.onChanged();
            } else {
               this.operationBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxCrud.UpdateOperation.Builder getOperationBuilder(int index) {
            return this.getOperationFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxCrud.UpdateOperationOrBuilder getOperationOrBuilder(int index) {
            return this.operationBuilder_ == null ? this.operation_.get(index) : this.operationBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxCrud.UpdateOperationOrBuilder> getOperationOrBuilderList() {
            return this.operationBuilder_ != null ? this.operationBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.operation_);
         }

         public MysqlxCrud.UpdateOperation.Builder addOperationBuilder() {
            return this.getOperationFieldBuilder().addBuilder(MysqlxCrud.UpdateOperation.getDefaultInstance());
         }

         public MysqlxCrud.UpdateOperation.Builder addOperationBuilder(int index) {
            return this.getOperationFieldBuilder().addBuilder(index, MysqlxCrud.UpdateOperation.getDefaultInstance());
         }

         public List<MysqlxCrud.UpdateOperation.Builder> getOperationBuilderList() {
            return this.getOperationFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxCrud.UpdateOperation, MysqlxCrud.UpdateOperation.Builder, MysqlxCrud.UpdateOperationOrBuilder> getOperationFieldBuilder() {
            if (this.operationBuilder_ == null) {
               this.operationBuilder_ = new RepeatedFieldBuilderV3<>(this.operation_, (this.bitField0_ & 32) != 0, this.getParentForChildren(), this.isClean());
               this.operation_ = null;
            }

            return this.operationBuilder_;
         }

         private void ensureArgsIsMutable() {
            if ((this.bitField0_ & 64) == 0) {
               this.args_ = new ArrayList<>(this.args_);
               this.bitField0_ |= 64;
            }
         }

         @Override
         public List<MysqlxDatatypes.Scalar> getArgsList() {
            return this.argsBuilder_ == null ? Collections.unmodifiableList(this.args_) : this.argsBuilder_.getMessageList();
         }

         @Override
         public int getArgsCount() {
            return this.argsBuilder_ == null ? this.args_.size() : this.argsBuilder_.getCount();
         }

         @Override
         public MysqlxDatatypes.Scalar getArgs(int index) {
            return this.argsBuilder_ == null ? this.args_.get(index) : this.argsBuilder_.getMessage(index);
         }

         public MysqlxCrud.Update.Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.set(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addArgs(MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxCrud.Update.Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.args_);
               this.onChanged();
            } else {
               this.argsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxCrud.Update.Builder clearArgs() {
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -65;
               this.onChanged();
            } else {
               this.argsBuilder_.clear();
            }

            return this;
         }

         public MysqlxCrud.Update.Builder removeArgs(int index) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.remove(index);
               this.onChanged();
            } else {
               this.argsBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
            return this.getArgsFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
            return this.argsBuilder_ == null ? this.args_.get(index) : this.argsBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
            return this.argsBuilder_ != null ? this.argsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.args_);
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
            return this.getArgsFieldBuilder().addBuilder(MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
            return this.getArgsFieldBuilder().addBuilder(index, MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
            return this.getArgsFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
            if (this.argsBuilder_ == null) {
               this.argsBuilder_ = new RepeatedFieldBuilderV3<>(this.args_, (this.bitField0_ & 64) != 0, this.getParentForChildren(), this.isClean());
               this.args_ = null;
            }

            return this.argsBuilder_;
         }

         @Override
         public boolean hasLimitExpr() {
            return (this.bitField0_ & 128) != 0;
         }

         @Override
         public MysqlxCrud.LimitExpr getLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            } else {
               return this.limitExprBuilder_.getMessage();
            }
         }

         public MysqlxCrud.Update.Builder setLimitExpr(MysqlxCrud.LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limitExpr_ = value;
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(value);
            }

            this.bitField0_ |= 128;
            return this;
         }

         public MysqlxCrud.Update.Builder setLimitExpr(MysqlxCrud.LimitExpr.Builder builderForValue) {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 128;
            return this;
         }

         public MysqlxCrud.Update.Builder mergeLimitExpr(MysqlxCrud.LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if ((this.bitField0_ & 128) != 0 && this.limitExpr_ != null && this.limitExpr_ != MysqlxCrud.LimitExpr.getDefaultInstance()) {
                  this.limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
               } else {
                  this.limitExpr_ = value;
               }

               this.onChanged();
            } else {
               this.limitExprBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 128;
            return this;
         }

         public MysqlxCrud.Update.Builder clearLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
               this.onChanged();
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -129;
            return this;
         }

         public MysqlxCrud.LimitExpr.Builder getLimitExprBuilder() {
            this.bitField0_ |= 128;
            this.onChanged();
            return this.getLimitExprFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
            if (this.limitExprBuilder_ != null) {
               return this.limitExprBuilder_.getMessageOrBuilder();
            } else {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> getLimitExprFieldBuilder() {
            if (this.limitExprBuilder_ == null) {
               this.limitExprBuilder_ = new SingleFieldBuilderV3<>(this.getLimitExpr(), this.getParentForChildren(), this.isClean());
               this.limitExpr_ = null;
            }

            return this.limitExprBuilder_;
         }

         public final MysqlxCrud.Update.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Update.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.Update.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.Update.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public static final class UpdateOperation extends GeneratedMessageV3 implements MysqlxCrud.UpdateOperationOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int SOURCE_FIELD_NUMBER = 1;
      private MysqlxExpr.ColumnIdentifier source_;
      public static final int OPERATION_FIELD_NUMBER = 2;
      private int operation_;
      public static final int VALUE_FIELD_NUMBER = 3;
      private MysqlxExpr.Expr value_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCrud.UpdateOperation DEFAULT_INSTANCE = new MysqlxCrud.UpdateOperation();
      @Deprecated
      public static final Parser<MysqlxCrud.UpdateOperation> PARSER = new AbstractParser<MysqlxCrud.UpdateOperation>() {
         public MysqlxCrud.UpdateOperation parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCrud.UpdateOperation(input, extensionRegistry);
         }
      };

      private UpdateOperation(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private UpdateOperation() {
         this.operation_ = 1;
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCrud.UpdateOperation();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private UpdateOperation(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxExpr.ColumnIdentifier.Builder subBuilderx = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilderx = this.source_.toBuilder();
                        }

                        this.source_ = input.readMessage(MysqlxExpr.ColumnIdentifier.PARSER, extensionRegistry);
                        if (subBuilderx != null) {
                           subBuilderx.mergeFrom(this.source_);
                           this.source_ = subBuilderx.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        int rawValue = input.readEnum();
                        MysqlxCrud.UpdateOperation.UpdateType value = MysqlxCrud.UpdateOperation.UpdateType.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(2, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.operation_ = rawValue;
                        }
                        break;
                     case 26:
                        MysqlxExpr.Expr.Builder subBuilder = null;
                        if ((this.bitField0_ & 4) != 0) {
                           subBuilder = this.value_.toBuilder();
                        }

                        this.value_ = input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.value_);
                           this.value_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 4;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw new InvalidProtocolBufferException(var14).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCrud.UpdateOperation.class, MysqlxCrud.UpdateOperation.Builder.class);
      }

      @Override
      public boolean hasSource() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxExpr.ColumnIdentifier getSource() {
         return this.source_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;
      }

      @Override
      public MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder() {
         return this.source_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;
      }

      @Override
      public boolean hasOperation() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public MysqlxCrud.UpdateOperation.UpdateType getOperation() {
         MysqlxCrud.UpdateOperation.UpdateType result = MysqlxCrud.UpdateOperation.UpdateType.valueOf(this.operation_);
         return result == null ? MysqlxCrud.UpdateOperation.UpdateType.SET : result;
      }

      @Override
      public boolean hasValue() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public MysqlxExpr.Expr getValue() {
         return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getValueOrBuilder() {
         return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasSource()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.hasOperation()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getSource().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasValue() && !this.getValue().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getSource());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.operation_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeMessage(3, this.getValue());
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getSource());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.operation_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeMessageSize(3, this.getValue());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxCrud.UpdateOperation)) {
            return super.equals(obj);
         } else {
            MysqlxCrud.UpdateOperation other = (MysqlxCrud.UpdateOperation)obj;
            if (this.hasSource() != other.hasSource()) {
               return false;
            } else if (this.hasSource() && !this.getSource().equals(other.getSource())) {
               return false;
            } else if (this.hasOperation() != other.hasOperation()) {
               return false;
            } else if (this.hasOperation() && this.operation_ != other.operation_) {
               return false;
            } else if (this.hasValue() != other.hasValue()) {
               return false;
            } else {
               return this.hasValue() && !this.getValue().equals(other.getValue()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasSource()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getSource().hashCode();
            }

            if (this.hasOperation()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.operation_;
            }

            if (this.hasValue()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getValue().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCrud.UpdateOperation parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.UpdateOperation parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.UpdateOperation parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.UpdateOperation parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.UpdateOperation parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCrud.UpdateOperation parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCrud.UpdateOperation parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.UpdateOperation parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.UpdateOperation parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCrud.UpdateOperation parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCrud.UpdateOperation parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCrud.UpdateOperation parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCrud.UpdateOperation.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCrud.UpdateOperation.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCrud.UpdateOperation.Builder newBuilder(MysqlxCrud.UpdateOperation prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCrud.UpdateOperation.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCrud.UpdateOperation.Builder() : new MysqlxCrud.UpdateOperation.Builder().mergeFrom(this);
      }

      protected MysqlxCrud.UpdateOperation.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCrud.UpdateOperation.Builder(parent);
      }

      public static MysqlxCrud.UpdateOperation getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCrud.UpdateOperation> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCrud.UpdateOperation> getParserForType() {
         return PARSER;
      }

      public MysqlxCrud.UpdateOperation getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCrud.UpdateOperation.Builder> implements MysqlxCrud.UpdateOperationOrBuilder {
         private int bitField0_;
         private MysqlxExpr.ColumnIdentifier source_;
         private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> sourceBuilder_;
         private int operation_ = 1;
         private MysqlxExpr.Expr value_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> valueBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCrud.UpdateOperation.class, MysqlxCrud.UpdateOperation.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.UpdateOperation.alwaysUseFieldBuilders) {
               this.getSourceFieldBuilder();
               this.getValueFieldBuilder();
            }
         }

         public MysqlxCrud.UpdateOperation.Builder clear() {
            super.clear();
            if (this.sourceBuilder_ == null) {
               this.source_ = null;
            } else {
               this.sourceBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.operation_ = 1;
            this.bitField0_ &= -3;
            if (this.valueBuilder_ == null) {
               this.value_ = null;
            } else {
               this.valueBuilder_.clear();
            }

            this.bitField0_ &= -5;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
         }

         public MysqlxCrud.UpdateOperation getDefaultInstanceForType() {
            return MysqlxCrud.UpdateOperation.getDefaultInstance();
         }

         public MysqlxCrud.UpdateOperation build() {
            MysqlxCrud.UpdateOperation result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCrud.UpdateOperation buildPartial() {
            MysqlxCrud.UpdateOperation result = new MysqlxCrud.UpdateOperation(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.sourceBuilder_ == null) {
                  result.source_ = this.source_;
               } else {
                  result.source_ = this.sourceBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.operation_ = this.operation_;
            if ((from_bitField0_ & 4) != 0) {
               if (this.valueBuilder_ == null) {
                  result.value_ = this.value_;
               } else {
                  result.value_ = this.valueBuilder_.build();
               }

               to_bitField0_ |= 4;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCrud.UpdateOperation.Builder clone() {
            return (MysqlxCrud.UpdateOperation.Builder)super.clone();
         }

         public MysqlxCrud.UpdateOperation.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.UpdateOperation.Builder)super.setField(field, value);
         }

         public MysqlxCrud.UpdateOperation.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCrud.UpdateOperation.Builder)super.clearField(field);
         }

         public MysqlxCrud.UpdateOperation.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCrud.UpdateOperation.Builder)super.clearOneof(oneof);
         }

         public MysqlxCrud.UpdateOperation.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCrud.UpdateOperation.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCrud.UpdateOperation.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCrud.UpdateOperation.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCrud.UpdateOperation.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCrud.UpdateOperation) {
               return this.mergeFrom((MysqlxCrud.UpdateOperation)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCrud.UpdateOperation.Builder mergeFrom(MysqlxCrud.UpdateOperation other) {
            if (other == MysqlxCrud.UpdateOperation.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasSource()) {
                  this.mergeSource(other.getSource());
               }

               if (other.hasOperation()) {
                  this.setOperation(other.getOperation());
               }

               if (other.hasValue()) {
                  this.mergeValue(other.getValue());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasSource()) {
               return false;
            } else if (!this.hasOperation()) {
               return false;
            } else {
               return !this.getSource().isInitialized() ? false : !this.hasValue() || this.getValue().isInitialized();
            }
         }

         public MysqlxCrud.UpdateOperation.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCrud.UpdateOperation parsedMessage = null;

            try {
               parsedMessage = MysqlxCrud.UpdateOperation.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCrud.UpdateOperation)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasSource() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxExpr.ColumnIdentifier getSource() {
            if (this.sourceBuilder_ == null) {
               return this.source_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;
            } else {
               return this.sourceBuilder_.getMessage();
            }
         }

         public MysqlxCrud.UpdateOperation.Builder setSource(MysqlxExpr.ColumnIdentifier value) {
            if (this.sourceBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.source_ = value;
               this.onChanged();
            } else {
               this.sourceBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.UpdateOperation.Builder setSource(MysqlxExpr.ColumnIdentifier.Builder builderForValue) {
            if (this.sourceBuilder_ == null) {
               this.source_ = builderForValue.build();
               this.onChanged();
            } else {
               this.sourceBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.UpdateOperation.Builder mergeSource(MysqlxExpr.ColumnIdentifier value) {
            if (this.sourceBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.source_ != null && this.source_ != MysqlxExpr.ColumnIdentifier.getDefaultInstance()) {
                  this.source_ = MysqlxExpr.ColumnIdentifier.newBuilder(this.source_).mergeFrom(value).buildPartial();
               } else {
                  this.source_ = value;
               }

               this.onChanged();
            } else {
               this.sourceBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxCrud.UpdateOperation.Builder clearSource() {
            if (this.sourceBuilder_ == null) {
               this.source_ = null;
               this.onChanged();
            } else {
               this.sourceBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder getSourceBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getSourceFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder() {
            if (this.sourceBuilder_ != null) {
               return this.sourceBuilder_.getMessageOrBuilder();
            } else {
               return this.source_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> getSourceFieldBuilder() {
            if (this.sourceBuilder_ == null) {
               this.sourceBuilder_ = new SingleFieldBuilderV3<>(this.getSource(), this.getParentForChildren(), this.isClean());
               this.source_ = null;
            }

            return this.sourceBuilder_;
         }

         @Override
         public boolean hasOperation() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxCrud.UpdateOperation.UpdateType getOperation() {
            MysqlxCrud.UpdateOperation.UpdateType result = MysqlxCrud.UpdateOperation.UpdateType.valueOf(this.operation_);
            return result == null ? MysqlxCrud.UpdateOperation.UpdateType.SET : result;
         }

         public MysqlxCrud.UpdateOperation.Builder setOperation(MysqlxCrud.UpdateOperation.UpdateType value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.operation_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxCrud.UpdateOperation.Builder clearOperation() {
            this.bitField0_ &= -3;
            this.operation_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasValue() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public MysqlxExpr.Expr getValue() {
            if (this.valueBuilder_ == null) {
               return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
            } else {
               return this.valueBuilder_.getMessage();
            }
         }

         public MysqlxCrud.UpdateOperation.Builder setValue(MysqlxExpr.Expr value) {
            if (this.valueBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.value_ = value;
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public MysqlxCrud.UpdateOperation.Builder setValue(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.value_ = builderForValue.build();
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 4;
            return this;
         }

         public MysqlxCrud.UpdateOperation.Builder mergeValue(MysqlxExpr.Expr value) {
            if (this.valueBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0 && this.value_ != null && this.value_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.value_ = MysqlxExpr.Expr.newBuilder(this.value_).mergeFrom(value).buildPartial();
               } else {
                  this.value_ = value;
               }

               this.onChanged();
            } else {
               this.valueBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public MysqlxCrud.UpdateOperation.Builder clearValue() {
            if (this.valueBuilder_ == null) {
               this.value_ = null;
               this.onChanged();
            } else {
               this.valueBuilder_.clear();
            }

            this.bitField0_ &= -5;
            return this;
         }

         public MysqlxExpr.Expr.Builder getValueBuilder() {
            this.bitField0_ |= 4;
            this.onChanged();
            return this.getValueFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getValueOrBuilder() {
            if (this.valueBuilder_ != null) {
               return this.valueBuilder_.getMessageOrBuilder();
            } else {
               return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getValueFieldBuilder() {
            if (this.valueBuilder_ == null) {
               this.valueBuilder_ = new SingleFieldBuilderV3<>(this.getValue(), this.getParentForChildren(), this.isClean());
               this.value_ = null;
            }

            return this.valueBuilder_;
         }

         public final MysqlxCrud.UpdateOperation.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.UpdateOperation.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCrud.UpdateOperation.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCrud.UpdateOperation.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static enum UpdateType implements ProtocolMessageEnum {
         SET(1),
         ITEM_REMOVE(2),
         ITEM_SET(3),
         ITEM_REPLACE(4),
         ITEM_MERGE(5),
         ARRAY_INSERT(6),
         ARRAY_APPEND(7),
         MERGE_PATCH(8);

         public static final int SET_VALUE = 1;
         public static final int ITEM_REMOVE_VALUE = 2;
         public static final int ITEM_SET_VALUE = 3;
         public static final int ITEM_REPLACE_VALUE = 4;
         public static final int ITEM_MERGE_VALUE = 5;
         public static final int ARRAY_INSERT_VALUE = 6;
         public static final int ARRAY_APPEND_VALUE = 7;
         public static final int MERGE_PATCH_VALUE = 8;
         private static final Internal.EnumLiteMap<MysqlxCrud.UpdateOperation.UpdateType> internalValueMap = new Internal.EnumLiteMap<MysqlxCrud.UpdateOperation.UpdateType>() {
            public MysqlxCrud.UpdateOperation.UpdateType findValueByNumber(int number) {
               return MysqlxCrud.UpdateOperation.UpdateType.forNumber(number);
            }
         };
         private static final MysqlxCrud.UpdateOperation.UpdateType[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxCrud.UpdateOperation.UpdateType valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxCrud.UpdateOperation.UpdateType forNumber(int value) {
            switch (value) {
               case 1:
                  return SET;
               case 2:
                  return ITEM_REMOVE;
               case 3:
                  return ITEM_SET;
               case 4:
                  return ITEM_REPLACE;
               case 5:
                  return ITEM_MERGE;
               case 6:
                  return ARRAY_INSERT;
               case 7:
                  return ARRAY_APPEND;
               case 8:
                  return MERGE_PATCH;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<MysqlxCrud.UpdateOperation.UpdateType> internalGetValueMap() {
            return internalValueMap;
         }

         @Override
         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return getDescriptor().getValues().get(this.ordinal());
         }

         @Override
         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return MysqlxCrud.UpdateOperation.getDescriptor().getEnumTypes().get(0);
         }

         public static MysqlxCrud.UpdateOperation.UpdateType valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private UpdateType(int value) {
            this.value = value;
         }
      }
   }

   public interface UpdateOperationOrBuilder extends MessageOrBuilder {
      boolean hasSource();

      MysqlxExpr.ColumnIdentifier getSource();

      MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder();

      boolean hasOperation();

      MysqlxCrud.UpdateOperation.UpdateType getOperation();

      boolean hasValue();

      MysqlxExpr.Expr getValue();

      MysqlxExpr.ExprOrBuilder getValueOrBuilder();
   }

   public interface UpdateOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      MysqlxCrud.Collection getCollection();

      MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDataModel();

      MysqlxCrud.DataModel getDataModel();

      boolean hasCriteria();

      MysqlxExpr.Expr getCriteria();

      MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();

      boolean hasLimit();

      MysqlxCrud.Limit getLimit();

      MysqlxCrud.LimitOrBuilder getLimitOrBuilder();

      List<MysqlxCrud.Order> getOrderList();

      MysqlxCrud.Order getOrder(int var1);

      int getOrderCount();

      List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList();

      MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int var1);

      List<MysqlxCrud.UpdateOperation> getOperationList();

      MysqlxCrud.UpdateOperation getOperation(int var1);

      int getOperationCount();

      List<? extends MysqlxCrud.UpdateOperationOrBuilder> getOperationOrBuilderList();

      MysqlxCrud.UpdateOperationOrBuilder getOperationOrBuilder(int var1);

      List<MysqlxDatatypes.Scalar> getArgsList();

      MysqlxDatatypes.Scalar getArgs(int var1);

      int getArgsCount();

      List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();

      MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int var1);

      boolean hasLimitExpr();

      MysqlxCrud.LimitExpr getLimitExpr();

      MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder();
   }

   public static enum ViewAlgorithm implements ProtocolMessageEnum {
      UNDEFINED(1),
      MERGE(2),
      TEMPTABLE(3);

      public static final int UNDEFINED_VALUE = 1;
      public static final int MERGE_VALUE = 2;
      public static final int TEMPTABLE_VALUE = 3;
      private static final Internal.EnumLiteMap<MysqlxCrud.ViewAlgorithm> internalValueMap = new Internal.EnumLiteMap<MysqlxCrud.ViewAlgorithm>() {
         public MysqlxCrud.ViewAlgorithm findValueByNumber(int number) {
            return MysqlxCrud.ViewAlgorithm.forNumber(number);
         }
      };
      private static final MysqlxCrud.ViewAlgorithm[] VALUES = values();
      private final int value;

      @Override
      public final int getNumber() {
         return this.value;
      }

      @Deprecated
      public static MysqlxCrud.ViewAlgorithm valueOf(int value) {
         return forNumber(value);
      }

      public static MysqlxCrud.ViewAlgorithm forNumber(int value) {
         switch (value) {
            case 1:
               return UNDEFINED;
            case 2:
               return MERGE;
            case 3:
               return TEMPTABLE;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<MysqlxCrud.ViewAlgorithm> internalGetValueMap() {
         return internalValueMap;
      }

      @Override
      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return getDescriptor().getValues().get(this.ordinal());
      }

      @Override
      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return MysqlxCrud.getDescriptor().getEnumTypes().get(1);
      }

      public static MysqlxCrud.ViewAlgorithm valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private ViewAlgorithm(int value) {
         this.value = value;
      }
   }

   public static enum ViewCheckOption implements ProtocolMessageEnum {
      LOCAL(1),
      CASCADED(2);

      public static final int LOCAL_VALUE = 1;
      public static final int CASCADED_VALUE = 2;
      private static final Internal.EnumLiteMap<MysqlxCrud.ViewCheckOption> internalValueMap = new Internal.EnumLiteMap<MysqlxCrud.ViewCheckOption>() {
         public MysqlxCrud.ViewCheckOption findValueByNumber(int number) {
            return MysqlxCrud.ViewCheckOption.forNumber(number);
         }
      };
      private static final MysqlxCrud.ViewCheckOption[] VALUES = values();
      private final int value;

      @Override
      public final int getNumber() {
         return this.value;
      }

      @Deprecated
      public static MysqlxCrud.ViewCheckOption valueOf(int value) {
         return forNumber(value);
      }

      public static MysqlxCrud.ViewCheckOption forNumber(int value) {
         switch (value) {
            case 1:
               return LOCAL;
            case 2:
               return CASCADED;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<MysqlxCrud.ViewCheckOption> internalGetValueMap() {
         return internalValueMap;
      }

      @Override
      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return getDescriptor().getValues().get(this.ordinal());
      }

      @Override
      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return MysqlxCrud.getDescriptor().getEnumTypes().get(3);
      }

      public static MysqlxCrud.ViewCheckOption valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private ViewCheckOption(int value) {
         this.value = value;
      }
   }

   public static enum ViewSqlSecurity implements ProtocolMessageEnum {
      INVOKER(1),
      DEFINER(2);

      public static final int INVOKER_VALUE = 1;
      public static final int DEFINER_VALUE = 2;
      private static final Internal.EnumLiteMap<MysqlxCrud.ViewSqlSecurity> internalValueMap = new Internal.EnumLiteMap<MysqlxCrud.ViewSqlSecurity>() {
         public MysqlxCrud.ViewSqlSecurity findValueByNumber(int number) {
            return MysqlxCrud.ViewSqlSecurity.forNumber(number);
         }
      };
      private static final MysqlxCrud.ViewSqlSecurity[] VALUES = values();
      private final int value;

      @Override
      public final int getNumber() {
         return this.value;
      }

      @Deprecated
      public static MysqlxCrud.ViewSqlSecurity valueOf(int value) {
         return forNumber(value);
      }

      public static MysqlxCrud.ViewSqlSecurity forNumber(int value) {
         switch (value) {
            case 1:
               return INVOKER;
            case 2:
               return DEFINER;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<MysqlxCrud.ViewSqlSecurity> internalGetValueMap() {
         return internalValueMap;
      }

      @Override
      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return getDescriptor().getValues().get(this.ordinal());
      }

      @Override
      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return MysqlxCrud.getDescriptor().getEnumTypes().get(2);
      }

      public static MysqlxCrud.ViewSqlSecurity valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private ViewSqlSecurity(int value) {
         this.value = value;
      }
   }
}
