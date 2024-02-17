package com.example.marketplace.Admin;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplace.Model.CategoryModel;
import com.example.marketplace.R;
import com.example.marketplace.databinding.ActivityAdminThreeBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

public class AdminThreeActivity extends AppCompatActivity {
    FirebaseFirestore db;
    CollectionReference categoriesRef;
    private static final String TAG = "ADMIN_THREE_ACTIVITY";
    ActivityAdminThreeBinding binding;
    BaseTreeAdapter<Viewholder> adapter;
    TreeNode root = new TreeNode("Categories");
    List<TreeNode> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminThreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initGlobalFields();
    }

    private void initGlobalFields() {
        db = FirebaseFirestore.getInstance();
        categoriesRef = db.collection("Categories");

        TreeView treeView = binding.idTreeView;

        adapter = new BaseTreeAdapter<Viewholder>(this, R.layout.admin_tree_item) {
            @NonNull
            @Override
            public Viewholder onCreateViewHolder(View view) {
                return new Viewholder(view);
            }

            @Override
            public void onBindViewHolder(Viewholder viewHolder, Object data, int position) {
                viewHolder.textView.setText(data.toString());
                viewHolder.addChild.setOnClickListener(v -> addNewChild(getNode(position)));
                viewHolder.removeChild.setOnClickListener(v -> deleteSubCategory(getNode(position)));
            }
        };

        treeView.setAdapter(adapter);
        fetchCategories();
    }
    private void fetchCategories() {
        categoriesRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            TreeNode category = new TreeNode(document.getString("name"));

                            List<String> subCategories = (List<String>) document.get("subCategories");
                            if (subCategories != null) {
                                for (String subCategoryName : subCategories) {
                                    TreeNode subCategory = new TreeNode(subCategoryName);
                                    category.addChild(subCategory);
                                }
                            }

                            categories.add(category);
                            root.addChild(category);
                        }
                        adapter.setRootNode(root);
                    } else {
                        Log.e(TAG, "Failed to fetch data from Firestore", task.getException());
                    }
                });
    }
    private void addNewChild(TreeNode node) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Child Node");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String str = input.getText().toString();
            node.addChild(new TreeNode(str));
            saveInFirestore(String.valueOf(node.getData()), str);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void deleteSubCategory(TreeNode node) {
        String parentCategoryName = String.valueOf(node.getParent().getData());
        String subCategoryName = String.valueOf(node.getData());

        node.getParent().removeChild(node);

        categoriesRef.whereEqualTo("name", parentCategoryName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot parentDocument = queryDocumentSnapshots.getDocuments().get(0);

                        List<String> subCategories = (List<String>) parentDocument.get("subCategories");
                        if (subCategories != null && subCategories.contains(subCategoryName)) {
                            subCategories.remove(subCategoryName);

                            parentDocument.getReference().update("subCategories", subCategories)
                                    .addOnSuccessListener(aVoid ->
                                            Log.d(TAG, "Sub-category deleted successfully")
                                    )
                                    .addOnFailureListener(e ->
                                            Log.e(TAG, "Error updating parent document", e)
                                    );
                        } else {
                            Log.e(TAG, "Sub-category not found: " + subCategoryName);
                        }
                    } else {
                        Log.e(TAG, "Parent category not found: " + parentCategoryName);
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error fetching parent category", e)
                );
    }
    private void saveInFirestore(String parent, String child) {
        CategoryModel obj = new CategoryModel(child, parent);
        if (parent.equals("Categories")) {
            categoriesRef.add(obj)
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding document", e));
            return;
        }

        categoriesRef.whereEqualTo("name", parent)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        List<String> subCategories = (List<String>) document.get("subCategories");
                        if (subCategories == null) {
                            subCategories = new ArrayList<>();
                        }
                        subCategories.add(child);

                        document.getReference().update("subCategories", subCategories)
                                .addOnSuccessListener(aVoid ->
                                        Log.d(TAG, "Sub-category added successfully")
                                )
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Error updating parent document", e)
                                );
                    } else {
                        Log.e(TAG, "Parent category not found: " + parent);
                    }
                })
                .addOnFailureListener(e ->
                    Log.e(TAG, "Error fetching parent category", e)
                );
    }
    private static class Viewholder {
        TextView textView;
        ImageView addChild;
        ImageView removeChild;

        Viewholder(View view) {
            textView = view.findViewById(R.id.idTvnode);
            addChild = view.findViewById(R.id.addChild);
            removeChild = view.findViewById(R.id.removeChild);
        }
    }
}
