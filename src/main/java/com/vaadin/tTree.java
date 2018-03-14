package com.vaadin;


import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;

import java.sql.*;
import java.util.List;

/**
 * Created by kalistrat on 18.11.2016.
 */
public class tTree extends Tree {

    public HierarchicalContainer TreeContainer;

//    private static final Action ACTION_ADD = new Action("Добавить");
//    private static final Action ACTION_DELETE = new Action("Удалить");
//    private static final Action[] ACTIONS = new Action[] { ACTION_ADD,
//            ACTION_DELETE };

    //public String iUserLog;

    public tTree(String eUserLog,tMainView eMainView){

        TreeContainer = new HierarchicalContainer();

        TreeContainer.addContainerProperty(1, Integer.class, null);
        TreeContainer.addContainerProperty(2, Integer.class, null);
        TreeContainer.addContainerProperty(3, Integer.class, null);
        TreeContainer.addContainerProperty(4, String.class, null);
        TreeContainer.addContainerProperty(5, String.class, null);
        TreeContainer.addContainerProperty(6, Integer.class, null);
        TreeContainer.addContainerProperty(7, String.class, null);


        tTreeGetData(eUserLog);

        setItemCaptionPropertyId(4);

        setContainerDataSource(this.TreeContainer);

        //Разворачиваю дерево
        for (Object id : this.rootItemIds()) {
            this.expandItemsRecursively(id);

        }


        for (Object id : this.TreeContainer.getItemIds()){
            if (!this.TreeContainer.hasChildren(id))
                this.TreeContainer.setChildrenAllowed(id, false);
        }

        //this.expandItem(1);


        this.select(1);

        //this.addStyleName("captiontree");

        this.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(valueChangeEvent.getProperty().getValue() != null)
                {
                    //String atribut = getItemCaption(event.getProperty().getValue());
                    //System.out.println(event.getProperty().getValue());
                    Item SelectedItem = TreeContainer.getItem(valueChangeEvent.getProperty().getValue());
                    //SelectedItem.getItemProperty(5).getValue();
                    eMainView.TreeContentUsr.tTreeContentLayoutRefresh((int) SelectedItem.getItemProperty(2).getValue(),(int) SelectedItem.getItemProperty(6).getValue());

//                    int SelectedLeafId = (int) SelectedItem.getItemProperty(2).getValue();
//                    System.out.println("SelectedLeafId :" + SelectedLeafId);
//
//                    List<Integer> chList = eMainView.TreeContentUsr.getChildAllLeafsById(SelectedLeafId);
//
//                    for (Integer iL : chList) {
//                        System.out.println("iL :" + iL);
//                    }

                    //System.out.println("last iL :" + eMainView.TreeContentUsr.getChildAllLeafsById(SelectedLeafId).get(eMainView.TreeContentUsr.getChildAllLeafsById(SelectedLeafId).size()-1));

                }
            }
        });

//        this.addActionHandler(new Action.Handler() {
//            @Override
//            public Action[] getActions(Object o, Object o1) {
//                return ACTIONS;
//            }
//
//            @Override
//            public void handleAction(Action action, Object o, Object o1) {
//
//                if (action == ACTION_ADD) {
//                 System.out.println("Попытка добавления");
//                }
//
//                if (action == ACTION_DELETE) {
//                    System.out.println("Попытка удаления");
//                }
//
//            }
//        });

        //Добавляю слушатель для каждого листочка дерева
//        this.addListener(new Property.ValueChangeListener() {
//
//            public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
//                if(event.getProperty().getValue() != null)
//                {
//                    //String atribut = getItemCaption(event.getProperty().getValue());
//                    //System.out.println(event.getProperty().getValue());
//                    Item SelectedItem = TreeContainer.getItem(event.getProperty().getValue());
//                    //SelectedItem.getItemProperty(5).getValue();
//                    eMainView.TreeContentUsr.tTreeContentLayoutRefresh((int) SelectedItem.getItemProperty(2).getValue(),(int) SelectedItem.getItemProperty(6).getValue());
//
//                }
//            }
//        });

    }

    public void tTreeGetData(String qUserLog){


        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String TreeSql = "select udt.user_devices_tree_id\n" +
                    ",udt.leaf_id\n" +
                    ",ifnull(udt.parent_leaf_id,0)\n" +
                    ",udt.leaf_name\n" +
                    ",ifnull(act.icon_code,'FOLDER') icon_code\n" +
                    ",ifnull(udt.user_device_id,0) user_device_id\n" +
                    ",act.action_type_name\n" +
                    "from user_devices_tree udt\n" +
                    "join users u on u.user_id=udt.user_id\n" +
                    "left join user_device ud on ud.user_device_id=udt.user_device_id\n" +
                    "left join action_type act on act.action_type_id=ud.action_type_id\n" +
                    "where u.user_log=?\n" +
                    "order by udt.leaf_id";

            PreparedStatement TreeSqlStmt = Con.prepareStatement(TreeSql);
            TreeSqlStmt.setString(1,qUserLog);


            ResultSet TreeSqlRs = TreeSqlStmt.executeQuery();

            while (TreeSqlRs.next()) {

                Item newItem = TreeContainer.addItem(TreeSqlRs.getInt(2));
                Integer UsrDevTreeId = TreeSqlRs.getInt(1);
                Integer UsrLeafId = TreeSqlRs.getInt(2);
                Integer UsrParentLeafId = TreeSqlRs.getInt(3);
                String UsrLeafName = TreeSqlRs.getString(4);
                String UsrLeafIcon = TreeSqlRs.getString(5);
                Integer UsrDeviceId = TreeSqlRs.getInt(6);
                String UsrActionType = TreeSqlRs.getString(7);

                newItem.getItemProperty(1).setValue(UsrDevTreeId);
                newItem.getItemProperty(2).setValue(UsrLeafId);
                newItem.getItemProperty(3).setValue(UsrParentLeafId);
                newItem.getItemProperty(4).setValue(UsrLeafName);
                newItem.getItemProperty(5).setValue(UsrLeafIcon);
                newItem.getItemProperty(6).setValue(UsrDeviceId);
                newItem.getItemProperty(7).setValue(UsrActionType);
                //FontAwesome.SIGN_IN
                        //Button v = new Button("ere");
                //v.setIcon();

            }


            Con.close();

        } catch (SQLException se3) {
            //Handle errors for JDBC
            se3.printStackTrace();
        } catch (Exception e13) {
            //Handle errors for Class.forName
            e13.printStackTrace();
        }

        //String s1 = (String) iTreeContainer.getItem(1).getItemProperty(4).getValue();


        for (int i = 0; i < TreeContainer.size(); i++){

            if (((Integer) TreeContainer.getItem(i+1).getItemProperty(3).getValue()).intValue() != 0) {
                TreeContainer.setParent(i+1, TreeContainer.getItem(i+1).getItemProperty(3).getValue());
            }

        }

        for (int j=1;j<TreeContainer.size()+1;j++) {
            String IconStr =  (String) TreeContainer.getItem(j).getItemProperty(5).getValue();

            if (IconStr.equals("FOLDER")) {
                setItemIcon(j, VaadinIcons.FOLDER);
            }
            if (IconStr.equals("TACHOMETER")) {
                setItemIcon(j, FontAwesome.TACHOMETER);
            }
            if (IconStr.equals("AUTOMATION")) {
                setItemIcon(j, VaadinIcons.AUTOMATION);
            }
        }

    }



}
