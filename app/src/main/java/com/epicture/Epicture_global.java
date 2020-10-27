package com.epicture;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.epicture.Adapter.CommentsDatas;
import com.epicture.Adapter.FavoriteGalleryAdapter;
import com.epicture.Adapter.GalleryDatasAdapter;
import com.epicture.Adapter.PersonalGalleryAdapter;
import com.epicture.Callbacks.VolleyCallbacks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Epicture_global extends Application {
    //Global bool
    public static boolean mature;

    //WebView
    public static WebView myWebView = null;

    //Account_values
    public static String client_id = "0be29f86c5c606f";
    public static String access_token;
    public static String expires_in;
    public static String token_type;
    public static String refresh_token;
    public static String username;
    public static String account_id;
    public static String profileImageUrl;
    public static String bannerImageUrl;
    public static String reputation;
    public static String score;

    //Requested JSONs
    public static JSONObject AccountBase = null;
    public static ArrayList<GalleryDatasAdapter> GalleryData = new ArrayList<GalleryDatasAdapter>();
    public static ArrayList<GalleryDatasAdapter> PersonalGalleryData = new ArrayList<GalleryDatasAdapter>();
    public static ArrayList<GalleryDatasAdapter> FavoriteGalleryData = new ArrayList<GalleryDatasAdapter>();
    public static ArrayList<CommentsDatas> CommentsDatasList = new ArrayList<CommentsDatas>();
    public static ArrayList<String> FavoriteIds = new ArrayList<String>();
    public static ArrayList<String> upVoteIds = new ArrayList<String>();
    public static ArrayList<String> downVoteIds = new ArrayList<String>();
    public static JSONArray Gallery;

    //Fragment Adapters
    public static PersonalGalleryAdapter PersonalAdapter;
    public static FavoriteGalleryAdapter FavoriteAdapter;
    public static com.epicture.Adapter.GalleryAdapter GalleryAdapter;
    public static com.epicture.Adapter.CommentsAdapter CommentsAdapter;

    public static String getOrDefault(JSONObject obj, String key, String defValue) {
        try {
            String value = obj.getString(key);
            return value;
        } catch (Exception ex) {
            return defValue;
        }
    }

    public static void getGallery(final Context context, String urlFirst, String urlSecond, final VolleyCallbacks callbacks) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = null;
        Epicture_global.GalleryData.clear();
        if (!Epicture_global.mature) {
            url = "https://api.imgur.com/3/gallery/" + urlFirst + "/" + urlSecond + "/";
        } else if (Epicture_global.mature) {
            url = "https://api.imgur.com/3/gallery/" + urlFirst + "/" + urlSecond + "/?mature=true";
        }

        JsonObjectRequest galery = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        getFavoriteGallery(context, new VolleyCallbacks() {
                            @Override
                            public void onSuccessResponse(JSONObject favorites) {
                                try {
                                    GalleryDatasAdapter data;
                                    JSONArray rep = response.getJSONArray("data");
                                    //foreach in JSON
                                    for (int i = 0; i < rep.length(); i++) {
                                        if (rep.getJSONObject(i).has("images") && !rep.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("type").equals("video/mp4")) {
                                            data = new GalleryDatasAdapter();
                                            data.setTitle(rep.getJSONObject(i).getString("title"));
                                            data.setGalleryId(rep.getJSONObject(i).getString("id"));
                                            data.setVote(rep.getJSONObject(i).getString("vote"));
                                            data.setId(rep.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("id"));
                                            data.setUrl(rep.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("link"));
                                            data.setViews(rep.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("views"));
                                            data.setDescription(rep.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("description"));
                                            Epicture_global.GalleryData.add(data);
                                        }
                                    }
                                    callbacks.onSuccessResponse(response);
                                } catch (JSONException e) {
                                    Log.d("groot", e.getMessage());
                                    return;
                                }
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error Internet Connection", Toast.LENGTH_LONG);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                if (access_token == null) {
                    headers.put("Authorization", "Client-ID " + Epicture_global.client_id);
                } else {
                    headers.put("Authorization", "Bearer " + Epicture_global.access_token);
                }
                return headers;
            }
        };
        queue.add(galery);
    }

    public static void getSearchGallery(final Context context, String urlFirst, String urlSecond, String query, final VolleyCallbacks callbacks) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = null;
        if (Epicture_global.mature) {
            url = "https://api.imgur.com/3/gallery/search/" + urlFirst + "/" + urlSecond + "?q=" + query + "&mature=true";
        } else {
            url = "https://api.imgur.com/3/gallery/search/" + urlFirst + "/" + urlSecond + "?q=" + query;
        }

        Epicture_global.GalleryData.clear();
        Log.d("groot", url);
        JsonObjectRequest searchgalery = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            GalleryDatasAdapter data;
                            JSONArray rep = response.getJSONArray("data");
                            //foreach in JSON
                            for (int i = 0; i < rep.length(); i++) {
                                if (rep.getJSONObject(i).has("images") && !rep.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("type").equals("video/mp4")) {
                                    data = new GalleryDatasAdapter();
                                    data.setId(rep.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("id"));
                                    data.setUrl(rep.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("link"));
                                    data.setViews(rep.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("views"));
                                    data.setDescription(rep.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("description"));
                                    data.setGalleryId(rep.getJSONObject(i).getString("id"));
                                    data.setTitle(rep.getJSONObject(i).getString("title"));
                                    Epicture_global.GalleryData.add(data);
                                }
                            }
                            callbacks.onSuccessResponse(response);
                        } catch (JSONException e) {
                            Log.d("groot", "Error");
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error Internet Connection", Toast.LENGTH_LONG);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Client-ID " + Epicture_global.client_id);
                return headers;
            }
        };
        queue.add(searchgalery);
    }

    public static void getPersonalGallery(final Context context, final VolleyCallbacks callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.imgur.com/3/account/me/images";
        Epicture_global.PersonalGalleryData.clear();


        JsonObjectRequest personalgallery = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            GalleryDatasAdapter data;
                            JSONArray rep = response.getJSONArray("data");
                            //foreach in JSON
                            for (int i = 0; i < rep.length(); i++) {
                                data = new GalleryDatasAdapter();
                                data.setId(rep.getJSONObject(i).getString("id"));
                                data.setUrl(rep.getJSONObject(i).getString("link"));
                                data.setViews(rep.getJSONObject(i).getString("views"));
                                data.setVote(rep.getJSONObject(i).getString("vote"));
                                data.setTitle(rep.getJSONObject(i).getString("title"));
                                data.setDescription(rep.getJSONObject(i).getString("description"));
                                Epicture_global.PersonalGalleryData.add(data);
                            }
                            callback.onSuccessResponse(response);
                        } catch (JSONException e) {
                            Log.d("groot", "Error");
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error Internet Connection", Toast.LENGTH_LONG);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Bearer " + Epicture_global.access_token);
                return headers;
            }
        };
        queue.add(personalgallery);
    }

    public static void getFavoriteGallery(final Context context, final VolleyCallbacks callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.imgur.com/3/account/" + Epicture_global.username + "/favorites/";
        FavoriteGalleryData.clear();
        FavoriteIds.clear();

        JsonObjectRequest favoritegallery = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            GalleryDatasAdapter data;
                            JSONArray rep = response.getJSONArray("data");
                            //foreach in JSON
                            for (int i = 0; i < rep.length(); i++) {
                                data = new GalleryDatasAdapter();
                                if (rep.getJSONObject(i).getString("type").equals("image/png")) {
                                    data.setId(rep.getJSONObject(i).getString("id"));
                                    data.setUrl("https://i.imgur.com/" + rep.getJSONObject(i).getString("cover") + ".png");
                                } else if (rep.getJSONObject(i).getString("type").equals("image/jpeg")) {
                                    data.setId(rep.getJSONObject(i).getString("id"));
                                    data.setUrl("https://i.imgur.com/" + rep.getJSONObject(i).getString("cover") + ".jpg");
                                } else if (rep.getJSONObject(i).getString("type").equals("image/gif")) {
                                    data.setId(rep.getJSONObject(i).getString("id"));
                                    data.setUrl("https://i.imgur.com/" + rep.getJSONObject(i).getString("cover") + ".gif");
                                }
                                data.setViews(rep.getJSONObject(i).getString("views"));
                                data.setVote(rep.getJSONObject(i).getString("vote"));
                                data.setTitle(rep.getJSONObject(i).getString("title"));
                                data.setDescription(rep.getJSONObject(i).getString("description"));
                                handleFavoriteIds(data.getId(), "add");
                                Epicture_global.FavoriteGalleryData.add(data);
                            }
                            callback.onSuccessResponse(response);
                        } catch (JSONException e) {
                            Log.d("groot", "Error");
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error Internet Connection", Toast.LENGTH_LONG);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Bearer " + Epicture_global.access_token);
                return headers;
            }
        };
        queue.add(favoritegallery);
    }

    public static void getProfileImageUrl(final Context context, final VolleyCallbacks callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.imgur.com/3/account/me";

        JsonObjectRequest favoritegallery = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            profileImageUrl = response.getJSONObject("data").getString("avatar");
                            bannerImageUrl = response.getJSONObject("data").getString("cover");
                            reputation = response.getJSONObject("data").getString("reputation_name");
                            score = response.getJSONObject("data").getString("reputation");
                            callback.onSuccessResponse(response);
                        } catch (JSONException e) {
                            Log.d("groot", "Error");
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error Internet Connection", Toast.LENGTH_LONG);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Bearer " + Epicture_global.access_token);
                return headers;
            }
        };
        queue.add(favoritegallery);
    }

    public static void uploadImage(final Context context, String upload, String title, String description, final VolleyCallbacks callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.imgur.com/3/upload";
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("image", upload);
            jsonBody.put("title", title);
            jsonBody.put("description", description);
        } catch (JSONException e) {
            Toast.makeText(context, "An error occured", Toast.LENGTH_LONG);
            return;
        }

        final String body = jsonBody.toString();

        JsonObjectRequest uploadRequest = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("groot", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + Epicture_global.access_token);
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    return body == null ? null : body.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.d("groot", "Unsupported Encoding while trying to get the bytes of %s using %s" +
                            body);
                    return null;
                }
            }
        };
        queue.add(uploadRequest);
    }

    public static void favoriteImage(final Context context, final String id, final VolleyCallbacks callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        if (id == null)
            return;
        String url = "https://api.imgur.com/3/image/" + id + "/favorite";
        JSONObject jsonBody = new JSONObject();

        JsonObjectRequest favoriterequest = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("groot", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + Epicture_global.access_token);
                return headers;
            }
        };
        queue.add(favoriterequest);
    }

    public static void handleFavoriteIds(String id, String toDo) {
        if (FavoriteIds.contains(id) && toDo == "remove") {
            FavoriteIds.remove(FavoriteIds.indexOf(id));
        } else if (!FavoriteIds.contains(id) && toDo == "add") {
            FavoriteIds.add(id);
        }
        return;
    }

    public static void getImageVote(final Context context, String galleryId, final VolleyCallbacks callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.imgur.com/3/" + galleryId + "/votes";

        JsonObjectRequest getimagevote = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            callback.onSuccessResponse(data);
                        } catch (JSONException e) {
                            Log.d("groot", "Error");
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("groot", "error");
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Client-ID " + Epicture_global.client_id);
                return headers;
            }
        };
        queue.add(getimagevote);
    }

    public static void voteImage(final Context context, String gallerId, String vote, final VolleyCallbacks callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.imgur.com/3/" + gallerId + "/vote/" + vote;

        JsonObjectRequest imagevote = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("groot", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + Epicture_global.access_token);
                return headers;
            }
        };
        queue.add(imagevote);
    }

    public static void getGalleryInfo(final Context context, String id, final VolleyCallbacks callbacks) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.imgur.com/3/" + id;

        JsonObjectRequest galeryinfo = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        getFavoriteGallery(context, new VolleyCallbacks() {
                            @Override
                            public void onSuccessResponse(JSONObject favorites) {
                                try {
                                    JSONObject data = response.getJSONObject("data");
                                    callbacks.onSuccessResponse(data);
                                } catch (JSONException e) {
                                    Log.d("groot", e.getMessage());
                                    return;
                                }
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getMessage() != null) {
                            Log.d("groot", error.getMessage());
                        }
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Bearer " + Epicture_global.access_token);
                return headers;
            }
        };
        queue.add(galeryinfo);
    }

    public static void getImageComments(final Context context, String galleryId, final VolleyCallbacks callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.imgur.com/3/" + galleryId + "/comments";

        Epicture_global.CommentsDatasList.clear();
        JsonObjectRequest getcomments = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            CommentsDatas data;
                            JSONArray rep = response.getJSONArray("data");
                            //foreach in JSON
                            for (int i = 0; i < rep.length(); i++) {
                                data = new CommentsDatas();
                                data.setUsername(rep.getJSONObject(i).getString("author"));
                                data.setContent(rep.getJSONObject(i).getString("comment"));
                                data.ups = rep.getJSONObject(i).getString("ups");
                                data.down = rep.getJSONObject(i).getString("downs");
                                data.id = rep.getJSONObject(i).getString("id");
                                Epicture_global.CommentsDatasList.add(data);
                            }
                            callback.onSuccessResponse(response);
                        } catch (JSONException e) {
                            Log.d("groot", e.getMessage());
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("groot", "error");
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Bearer " + Epicture_global.access_token);
                return headers;
            }
        };
        queue.add(getcomments);
    }

    public static void voteComment(final Context context, String commentId, String vote, final VolleyCallbacks callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.imgur.com/3/comment/" + commentId + "/vote/" + vote;

        Log.d("groot", url);
        JsonObjectRequest commentvote = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("groot", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + Epicture_global.access_token);
                return headers;
            }
        };
        queue.add(commentvote);
    }
}

