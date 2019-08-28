package com.globalm.platform.fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipDrawable;
import android.support.design.chip.ChipGroup;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.globalm.platform.R;
import com.globalm.platform.activities.MessageActivity;
import com.globalm.platform.adapters.ContactsAdapter;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.Contact;
import com.globalm.platform.models.ContactModelNew;
import com.globalm.platform.models.GetContentListModel;
import com.globalm.platform.models.Tag;
import com.globalm.platform.network.GlobalmAPI;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.EndlessRecyclerViewScrollListener;
import com.globalm.platform.utils.SkillsDialogUtil;

import java.util.ArrayList;
import java.util.List;

public class MyContactsFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, ContactsAdapter.ContactActionListener {
    private int pageNumber = GlobalmAPI.FIRST_PAGE;
    private EditText mFieldSearchContacts;
    private EditText mFieldFilterName;
    private EditText mFieldFilterLocation;
    private RecyclerView mListMyContacts;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mButtonFilterContacts;
    private ImageView mButtonClearSearch;
    private ContactsAdapter mContactsAdapter;
    private ExpandableRelativeLayout mExpandableRelativeLayout;
    private Button mButtonApply;
    private Button mButtonClearFilters;
    private TextView mTextViewNothingFound;

    private ArrayList<Contact> mContactList = new ArrayList<>();
    private ArrayList<ContactModelNew> mContactModelsNew = new ArrayList<>();
    private ChipGroup skillsChipGroup;
    private SkillsDialogUtil skillsDialogUtil;
    private Tag skillToFilter;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        skillsDialogUtil = new SkillsDialogUtil();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_contacts, container, false);
        bindViews(rootView);
        setupView();
        getContacts();
        return rootView;
    }

    @Override
    public void onRefresh() {
        resetData();
        getContacts();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_filter_results:
                if (mExpandableRelativeLayout.isExpanded()) {
                    mExpandableRelativeLayout.collapse();
                } else {
                    mExpandableRelativeLayout.expand();
                }
                break;
            case R.id.contacts_filter_btn_apply:
                mExpandableRelativeLayout.collapse();
                resetData();
                getContacts();
                break;
            case R.id.contacts_filter_btn_clear:
                skillToFilter = null;
                setupSkills();
                mFieldFilterName.setText("");
                mFieldFilterLocation.setText("");
                mFieldSearchContacts.setText("");
                break;
        }
    }

    //region ContactsAdapter.ContactActionListener
    @Override
    public void onRemove(Contact contact) {
        RequestManager.getInstance().removeContact(contact.getId(), new CallbackListener<BaseResponseBody<List<Void>, Object[]>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, Object[]> o) {
                handleRemoveContact(contact);
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(R.string.an_error_has_occured);
            }
        });
    }

    @Override
    public void onDecline(Contact contact) {
        RequestManager.getInstance().rejectContactRequest(contact.getId(), new CallbackListener<BaseResponseBody<List<Void>, Object[]>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, Object[]> o) {
                RequestsFragment.SHOULD_REFRESH_REQUESTS_DATA = true;
                handleDeclineContactRequest(contact);
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(R.string.an_error_has_occured);
            }
        });
    }

    @Override
    public void onSendContactRequest(Contact contact) {
        RequestManager.getInstance().sendContactRequest(contact.getId(), new CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Object>>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentListModel<List<Object>>> o) {
                RequestsFragment.SHOULD_REFRESH_PENDING_DATA = true;
                handleSendContactRequest(contact);
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(R.string.an_error_has_occured);
            }
        });
    }

    @Override
    public void onCancelContactRequest(Contact contact) {
        RequestManager.getInstance().cancelContactRequest(contact.getId(), new CallbackListener<BaseResponseBody<List<Void>, Object[]>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, Object[]> o) {
                RequestsFragment.SHOULD_REFRESH_REQUESTS_DATA = true;

            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(R.string.an_error_has_occured);
            }
        });
    }

    @Override
    public void onAccept(Contact contact) {
        RequestManager.getInstance().acceptContactRequest(contact.getId(), new CallbackListener<BaseResponseBody<List<Void>, Object[]>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, Object[]> o) {
                RequestsFragment.SHOULD_REFRESH_REQUESTS_DATA = true;
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(R.string.an_error_has_occured);
            }
        });
    }

    @Override
    public void onSendMessage(Contact contact) {
        startActivity(new Intent(getContext(), MessageActivity.class));
    }

    @Override
    public void onOpen(Contact contact) {

    }
    //endregion

    private void getContacts() {
        CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>> callbackListener = new CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>> o) {
                if (!o.getError()) {
                    List<Contact> contacts = o.getData().getItems();
                    if (contacts != null && contacts.size() > 0) {
                        mContactList.addAll(contacts);
                        for (Contact c : contacts) {
                            mContactModelsNew.add(new ContactModelNew(c));
                        }
                    }
                }
                if (mContactModelsNew.size() == 0) {
                    if (isSearchPerformed() || isFilteringPerformed()) {
                        mTextViewNothingFound.setVisibility(View.VISIBLE);
                        mButtonClearFilters.setVisibility(View.VISIBLE);
                    } else {
                        mTextViewNothingFound.setVisibility(View.VISIBLE);
                        mButtonClearFilters.setVisibility(View.GONE);
                    }
                } else {
                    mTextViewNothingFound.setVisibility(View.GONE);
                    mButtonClearFilters.setVisibility(View.GONE);
                }
                mContactsAdapter.setData(mContactModelsNew);
                mSwipeRefreshLayout.setRefreshing(false);
                mEndlessRecyclerViewScrollListener.setLoading(false);
            }

            @Override
            public void onFailure(Throwable error) {
                mSwipeRefreshLayout.setRefreshing(false);
                mEndlessRecyclerViewScrollListener.setLoading(false);
                showMessage(R.string.an_error_has_occured);
            }
        };
        String q = mFieldSearchContacts.getText().toString();
        if (!TextUtils.isEmpty(q)) {
            //perform search
            RequestManager.getInstance().getContacts(pageNumber, q, callbackListener);
        } else {
            //perform filtering
            String name = mFieldFilterName.getText().toString();
            String location = mFieldFilterLocation.getText().toString();
            Integer skillId = null;
            if (skillToFilter != null) {
                skillId = skillToFilter.getId();
            }
            RequestManager.getInstance().getMyContacts(pageNumber, name + " " + location, skillId, callbackListener);
        }

    }

    private void resetData() {
        pageNumber = GlobalmAPI.FIRST_PAGE;
        mContactModelsNew.clear();
        mContactList.clear();
    }

    private void bindViews(View rootView) {
        mFieldSearchContacts = rootView.findViewById(R.id.field_search);
        mFieldFilterName = rootView.findViewById(R.id.contacts_filter_et_name);
        mFieldFilterLocation = rootView.findViewById(R.id.contacts_filter_et_location);
        mListMyContacts = rootView.findViewById(R.id.list_my_contacts);
        mButtonFilterContacts = rootView.findViewById(R.id.button_filter_results);
        mButtonClearSearch = rootView.findViewById(R.id.iv_clear_search_text);
        mExpandableRelativeLayout = rootView.findViewById(R.id.layout_expandable_contact_search);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        mButtonApply = rootView.findViewById(R.id.contacts_filter_btn_apply);
        skillsChipGroup = rootView.findViewById(R.id.skills_chip_group);
        mButtonClearFilters = rootView.findViewById(R.id.contacts_filter_btn_clear);
        mTextViewNothingFound = rootView.findViewById(R.id.contacts_filter_tv_nothing_found);
    }

    private void setupView() {
        setupSkills();
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mContactsAdapter = new ContactsAdapter(this);
        mListMyContacts.setLayoutManager(mLinearLayoutManager);
        mListMyContacts.setAdapter(mContactsAdapter);
        mListMyContacts.addOnScrollListener(mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                pageNumber++;
                getContacts();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mButtonApply.setOnClickListener(this);
        mButtonFilterContacts.setOnClickListener(this);
        mFieldSearchContacts.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                mExpandableRelativeLayout.collapse();
                closeKeyboard(mFieldSearchContacts);
                resetData();
                getContacts();
                return true;
            }
            return false;
        });
        mFieldSearchContacts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    resetData();
                    getContacts();
                    mButtonClearSearch.setVisibility(View.INVISIBLE);
                } else {
                    mButtonClearSearch.setVisibility(View.VISIBLE);
                }
            }
        });
        mButtonClearSearch.setOnClickListener((v) -> {
            mFieldSearchContacts.setText("");
            closeKeyboard(mFieldSearchContacts);
        });
        mButtonClearSearch.setVisibility(mFieldSearchContacts.getText().length() == 0 ? View.INVISIBLE : View.VISIBLE);
        mButtonApply.setPaintFlags(mButtonApply.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        skillsChipGroup.setOnClickListener(v -> {
            skillsDialogUtil.showTagDialog(getContext(), RequestManager.getInstance(),  getDialogCallback());
        });
        mTextViewNothingFound.setVisibility(View.GONE);
        mButtonClearFilters.setVisibility(View.GONE);
        mButtonClearFilters.setOnClickListener(this);
    }

    private boolean isSearchPerformed() {
        return mFieldSearchContacts.getText().length() > 0;
    }

    private boolean isFilteringPerformed() {
        return mFieldFilterName.getText().length() > 0
                || mFieldFilterLocation.getText().length() > 0
                || skillToFilter !=null;
    }

    private SkillsDialogUtil.DialogCallback<List<Tag>> getDialogCallback() {
        return new SkillsDialogUtil.DialogCallback<List<Tag>>() {
            @Override
            public void onOk(List<Tag> skills) {
                addSkills(skills);
            }

            @Override
            public void onCancel() { }

            @Override
            public void onApiFailure(Throwable error) {
                showMessage(error.getLocalizedMessage(), Toast.LENGTH_LONG);
            }
        };
    }

    private void addSkills(List<Tag> skills) {
        if (skills.size() > 0) {
            skillToFilter = skills.get(0);
        }
        setupSkills();
    }

    private void setupSkills() {
        skillsChipGroup.removeAllViews();
        if (skillToFilter != null) {
            Chip newChip = getChip(skillToFilter.getName(), skillToFilter.getId());
            skillsChipGroup.addView(newChip);
        }
    }

    private Chip getChip(String newChipText, int skillId) {
        if (getContext() == null) {
            return null;
        }

        Chip chip = new Chip(skillsChipGroup.getContext());
        chip.setChipDrawable(ChipDrawable.createFromResource(getContext(), R.xml.chip));
        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(newChipText);
        chip.setOnClickListener(v -> {
            skillsDialogUtil.showTagDialog(getContext(), RequestManager.getInstance(),  getDialogCallback());
        });
        chip.setOnCloseIconClickListener(v -> {
            skillToFilter = null;
            skillsChipGroup.removeView(chip);
        });
        return chip;
    }

    private void handleRemoveContact(Contact contact) {
        int pos = getContactPosition(contact);
        if (pos >= 0) {
            if (isSearchPerformed()) {
                ContactModelNew model = mContactModelsNew.get(pos);
                Contact c = model.getContact();
                if (c != null && c.getMetadata() != null) {
                    c.getMetadata().setConnected(false);
                    mContactsAdapter.setData(mContactModelsNew);
                }
            } else {
                mContactModelsNew.remove(pos);
                mContactList.remove(pos);//temp
            }
            mContactsAdapter.setData(mContactModelsNew);
            mContactsAdapter.notifyItemRemoved(pos);
        }
    }

    private void handleDeclineContactRequest(Contact contact) {
        int pos = getContactPosition(contact);
        if (pos >= 0) {
            ContactModelNew model = mContactModelsNew.get(pos);
            Contact c = model.getContact();
            if (c != null && c.getMetadata() != null) {
                c.getMetadata().setRequested(false);
                mContactsAdapter.setData(mContactModelsNew);
            }
        }
    }

    private void handleSendContactRequest(Contact contact) {
        int pos = getContactPosition(contact);
        if (pos >= 0) {
            ContactModelNew model = mContactModelsNew.get(pos);
            Contact c = model.getContact();
            if (c != null && c.getMetadata() != null) {
                c.getMetadata().setRequested(true);
                mContactsAdapter.setData(mContactModelsNew);
            }
        }
    }

    private int getContactPosition(Contact contact) {
        int pos = -1;
        for (int i = 0; i < mContactModelsNew.size(); i++) {
            ContactModelNew model = mContactModelsNew.get(i);
            if (model.getContact() != null && model.getContact().getId() == contact.getId()) {
                pos = i;
                break;
            }
        }
        return pos;
    }

}