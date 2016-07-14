package com.alorma.github.ui.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.alorma.github.bean.sync.GistSyncFavorite;
import com.alorma.github.bean.sync.IssueSyncFavorite;
import com.alorma.github.bean.sync.RepositorySyncFavorite;
import com.alorma.github.bean.sync.SyncFavorite;
import com.alorma.gitskarios.core.client.TokenProvider;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SyncFavoritesPresenter {

  private SyncCallbacks syncCallbacks;
  private FirebaseAuth mAuth;
  private FirebaseUser currentUser;

  public void load() {
    mAuth = FirebaseAuth.getInstance();

    if (mAuth.getCurrentUser() != null) {
      onUserLoaded(mAuth.getCurrentUser());
    } else {
      firebaseUserNotLogged();
    }
  }

  private void firebaseUserNotLogged() {
    if (TokenProvider.getInstance() != null) {
      String token = TokenProvider.getInstance().getToken();
      if (token != null) {
        AuthCredential credential = GithubAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this::firebaseLoginResult);
      }
    }
  }

  private void firebaseLoginResult(Task<AuthResult> task) {
    if (!task.isSuccessful()) {
      // TODO on error
    } else {
      onUserLoaded(mAuth.getCurrentUser());
    }
  }

  @NonNull
  private String getPathFromUser(FirebaseUser currentUser) {
    return currentUser.getUid();
  }

  private void onUserLoaded(FirebaseUser currentUser) {
    this.currentUser = currentUser;

    if (syncCallbacks != null) {
      syncCallbacks.enableUi();
    }

    String path = getPathFromUser(currentUser);
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(path);

    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        List<SyncFavorite> favorites = getSyncFavorites(dataSnapshot);
        showItems(favorites);
        myRef.removeEventListener(this);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    myRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        List<SyncFavorite> favorites = getSyncFavorites(dataSnapshot);
        showItems(favorites);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  @NonNull
  private List<SyncFavorite> getSyncFavorites(DataSnapshot dataSnapshot) {
    List<SyncFavorite> favorites = new ArrayList<>();
    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
      if (snapshot.getValue() != null && snapshot.getValue() instanceof Map) {
        SyncFavorite value = parseValue((Map<String, Object>) snapshot.getValue());
        favorites.add(value);
      }
    }
    return favorites;
  }

  @Nullable
  private SyncFavorite parseValue(Map<String, Object> value) {
    if (value.get("type").equals(SyncFavorite.Type.REPOSITORY.name())) {
      return new RepositorySyncFavorite(value);
    } else if (value.get("type").equals(SyncFavorite.Type.ISSUE.name())) {
      return new IssueSyncFavorite(value);
    } else if (value.get("type").equals(SyncFavorite.Type.GIST.name())) {
      return new GistSyncFavorite(value);
    }
    return null;
  }

  private void showItems(List<SyncFavorite> favorites) {
    syncCallbacks.showItems(favorites);
  }

  public void addRepo() {
    SyncFavorite syncFavorite = new RepositorySyncFavorite(UUID.randomUUID().toString(), UUID.randomUUID().toString());

    addValue(syncFavorite);
  }

  public void addIssue() {
    SyncFavorite syncFavorite = new IssueSyncFavorite(UUID.randomUUID().toString(), UUID.randomUUID().toString(), new Random().nextLong());

    addValue(syncFavorite);
  }

  public void addGist() {
    SyncFavorite syncFavorite = new GistSyncFavorite(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    addValue(syncFavorite);
  }

  private void addValue(SyncFavorite value) {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String path = getPathFromUser(currentUser);
    DatabaseReference myRef = database.getReference(path);

    String key = myRef.push().getKey();
    Map<String, Object> map = new HashMap<>();
    Map<String, Object> values = value.toMap();
    values.put("type", value.getType().name());
    map.put(key, values);

    myRef.updateChildren(map);
  }

  public void setSyncCallbacks(SyncCallbacks syncCallbacks) {
    this.syncCallbacks = syncCallbacks;
  }

  public interface SyncCallbacks {
    void enableUi();

    void showItems(List<SyncFavorite> favorites);
  }
}
