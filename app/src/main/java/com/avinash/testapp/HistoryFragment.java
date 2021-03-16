package com.avinash.testapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {


    private static final String TAG = HistoryFragment.class.getSimpleName();
    public static ArrayList<User> selectedList = new ArrayList<>();
    private static OnClickEventListener listener;
    private RecyclerView recyclerView;
    private ArrayList<User> fireList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter firebaseRecyclerAdapter;
    private Query reference;
    private String userId;
    private FirebaseFirestore mDatabase;
    private View mEmptyListMessage;
    private String organizationId;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        //todo : Add parameters
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseFirestore.getInstance();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.list);
        mEmptyListMessage = root.findViewById(R.id.emptyView);

        attachRecyclerViewAdapter();
        return root;
    }



    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = (OnClickEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MyInterface ");
        }


    }

    private void attachRecyclerViewAdapter() {

        //mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        reference = mDatabase.collection("emailList")
                .orderBy("modifiedOn");
        FirestoreRecyclerOptions<User> options =
                new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(reference, User.class)
                        .setLifecycleOwner(this)
                        .build();


        firebaseRecyclerAdapter = newAdapter(options);



        recyclerView.setAdapter(firebaseRecyclerAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

    }


    protected RecyclerView.Adapter newAdapter(FirestoreRecyclerOptions options) {

        return new FirestoreRecyclerAdapter<User, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User model) {
                //holder.bind(model);
                //holder.mView.setTag(position);
                holder.file = model;

                holder.txtName.setText(model.name);
                holder.txtEmail.setText(model.email);
                holder.ckhBoxSelected.setChecked(false);
                try {

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy, hh:mm a ", Locale.ENGLISH);

                    holder.txtTime.setText(String.format(Locale.ENGLISH, "%s ",
                            sdf.format(model.getCreatedOn())));


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (selectedList.size() > 0) {
                    if (selectedList.contains(model)) {
                        holder.txtName.setAlpha(1f);
                        holder.ckhBoxSelected.setChecked(true);
                    }
                }
            }

            @Override
            public void onDataChanged() {

                // If there are no chat messages, show a view that invites the user to add a message.
                mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        //getActivity().setTitle("Previous Recordings");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.history, menu);
        MenuItem delete = menu.findItem(R.id.action_delete);
        delete.setVisible(selectedList.size() > 0);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity_schedular in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            deleteFiles();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void deleteFiles() {
        ArrayList<User> selectedPathList = selectedList;
        for (User fireFile : selectedPathList) {
            mDatabase.collection("emailList").document(fireFile.getDocumentId()).delete();
        }
        selectedPathList.clear();

        //adapter.notifyDataSetChanged();
    }

    public interface OnClickEventListener {
        void OnItemClick(User test);
        void OnItemsSelected(int size);
    }

    @Keep
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView txtName;
        public final TextView txtEmail;
        public final TextView txtTime;
        public final ImageButton imgBtnEdit;
        public final CheckBox ckhBoxSelected;

        public User file;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtEmail = (TextView) view.findViewById(R.id.txtEmail);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            imgBtnEdit = (ImageButton) view.findViewById(R.id.imgBtnEdit);
            ckhBoxSelected = (CheckBox) view.findViewById(R.id.ckhBoxSelected);


            mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ckhBoxSelected.performClick();
                    return true;
                }
            });

            ckhBoxSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mView.setAlpha(1f);
                        if (!HistoryFragment.selectedList.contains(file))
                            HistoryFragment.selectedList.add(file);
                    } else {
                        mView.setAlpha(0.5f);
                        if (HistoryFragment.selectedList.contains(file))
                            HistoryFragment.selectedList.remove(file);

                    }
                    if (listener != null)
                        listener.OnItemsSelected(HistoryFragment.selectedList.size());
                }
            });


            imgBtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnItemClick(file);
                    }

                }
            });


        }

        @Override
        public String toString() {
            return super.toString();
        }

    }

}
