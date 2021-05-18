package com.example.btl.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.btl.JoinActivity;
import com.example.btl.R;
import com.example.btl.RoomActivity;
import com.example.btl.adapter.AdapterUser;
import com.example.btl.model.Profile;
import com.example.btl.model.User;
import com.example.btl.utils.ClickItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment {
    private RecyclerView mRecyclerview;
    private AdapterUser adapterUser;
    private List<User> userList;
    private FirebaseUser user;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        initUitls();
        getDataByFirebase();
    }

    private void initUitls() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userList = new ArrayList<>();
        adapterUser = new AdapterUser(userList);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 6));
        mRecyclerview.setAdapter(adapterUser);
        adapterUser.setOnClickItem(new ClickItem() {
            @Override
            public void click(int position) {
                if (!userList.get(position).getUid().equals(user.getUid())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                    View view = LayoutInflater.from(Objects.requireNonNull(getContext())).inflate(R.layout.show_user_dialog, null);
                    TextView txtN = view.findViewById(R.id.txtN);
                    TextView txtFB = view.findViewById(R.id.txtFB);
                    TextView txtEmail = view.findViewById(R.id.txtEmail);
                    TextView txtSDT = view.findViewById(R.id.txtSDT);
                    CircleImageView mAvatar = view.findViewById(R.id.mAvatar);
                    builder.setView(view);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(userList.get(position).getUid());
                    reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            Profile md = dataSnapshot.getValue(Profile.class);
                            assert md != null;
                            txtN.setText(md.getPersonName());
                            txtFB.setText(md.getPersonFB());
                            txtEmail.setText(md.getPersonEmail());
                            txtSDT.setText(md.getPersonPhone());
                            Glide.with(getContext())
                                    .load(md.getPersonPhoto())
                                    .centerCrop()
                                    .into(mAvatar);
                        }
                    });
                }

            }
        });
    }

    private void getDataByFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("room").child(RoomActivity.id_room).child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    User model = s.getValue(User.class);
                    userList.add(model);
                }
                adapterUser.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void init(View view) {
        mRecyclerview = view.findViewById(R.id.mRecyclerview);
    }
}