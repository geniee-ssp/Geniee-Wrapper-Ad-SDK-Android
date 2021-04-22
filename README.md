# GNWrapperAdSDKの実装方法について

## 1. 概要
GNWrapperAdSDKは株式会社ジーニーが提供するアプリにおける収益最大化を行うためのSDKです。このSDKは以下の機能を提供します。

1. GoogleAdManagerの[UPR](https://support.google.com/admanager/answer/9298008?hl=ja)(Unified pricing rules)最適化
2. 各HeaderBidding SDKの最適化
	- [Prebid](https://docs.prebid.org/prebid-mobile/prebid-mobile.html)
	- [Pubmatic](https://community.pubmatic.com/display/OB/OpenWrap+SDK+Home)

## 2. 前提条件
実装にあたって以下の前提条件があります。

| 項目| 条件 | 
| :--  | :-- |
| Android Studio version | Android Studio 3.2以降 |
| Android version | Android 4.4以降 |

現在対応しているフォーマットは以下のとおりです。

| 項目| 条件 | 
| :--  | :-- |
| 対応フォーマット | Banner |
| 対応サイズ| 320x50<br>320x250 |

## 3. 事前準備
事前にジーニー担当者に連絡を行い、以下の事前準備を行ってください。

### 3.1 Firebase初期設定
1. Firebase管理画面よりプロジェクトを作成し、アプリの登録を行います。

2. アプリを登録する際、Firebaseへアクセスするための情報が記載されたファイル`google-services.json`をダウンロードする手順があるためダウンロードします。(または、登録したアプリの設定画面よりファイルをダウンロードできます。)

3. ダウンロードしたファイル`google-services.json`を、アプリのフォルダ直下へ追加します。  

	Firebase初期設定については以下のURLを参考にしてください。<br>
	[Android プロジェクトに Firebase を追加する](https://firebase.google.com/docs/android/setup?hl=ja)

### 3.2. Firebaseで使用するKey名の決定
1. ジーニー担当者と表示する広告の位置や数と広告に対するFirebase Remote Configのkey名を決定し、Firebase Remote Configの設定を完了させます。

### 3.3. Google Ad Managerでアプリケーションを登録してアプリケーションIDを取得する
1. Google Ad Managerでアプリを登録します。登録時にアプリケーションIDが発行されるため、アプリケーションIDを`AndroidManifest.xml`に登録します(登録方法は下記に示します。)。アプリケーションIDがわからない場合はジーニーの担当者に連絡をお願いします。

## 4. 実装手順

#### 4.1. build.gradleの設定
1. `Project>build.gradle`に以下の記述を追加してください。

	```
	buildscript {
   		repositories {
			google()
			jcenter()
		}
		dependencies {
    		// To integrate irebase Service SDK
			classpath 'com.google.gms:google-services:4.3.5'
		}
	}
	
	allprojects {
		repositories {
			google()
			jcenter()
			
			// To integrate Geniee Wrapper SDK
			maven {
				url 'https://raw.github.com/geniee-ssp/Geniee-Wrapper-Ad-SDK-Android/master/repository'
			}
		}
		
	}
	```
	
	- Pubmatic SDKを使用する場合は以下を追加してください。
	
	```
	buildscript {
   		repositories {
			// To integrate Pubmatic SDK
			maven {
				url 'https://repo.pubmatic.com/artifactory/public-repos'
			}   			       	
		}
	}
	
	allprojects {
		repositories {
			// To integrate Pubmatic SDK
			maven {
				url 'https://repo.pubmatic.com/artifactory/public-repos'
			}   			       	
		}
	}
	```
	
2. `Module>build.gradle`に以下の記述を追加してください。
	
	```
	apply plugin: 'com.google.gms.google-services'
	
	dependencies {
		implementation 'androidx.appcompat:appcompat:1.2.0'
		
		
    	// To integrate  Firebase Service SDK
		implementation platform('com.google.firebase:firebase-bom:26.2.0')
		implementation 'com.google.firebase:firebase-analytics'
		implementation 'com.google.firebase:firebase-config'
		
		
		// To integrate  Google Mobile Ads SDK
		implementation 'com.google.android.gms:play-services-ads:19.7.0'
		
		// To integrate  Geniee Wrapper Ad SDK
		implementation 'jp.co.geniee.gnwrapperadsdk:GNWrapperAdSDK:1.0.1'
	}
	```
	
	- Prebid SDKを使用する場合は以下を追加してください。
	
	```
	dependencies {
		// To integrate  Geniee Wrapper Ad Adapter
		implementation 'jp.co.geniee.gnhbadapter:GNHBPrebidAdapter:1.0.0.0'
		
		
		// To integrate Prebid Mobile SDK
    	implementation 'org.prebid:prebid-mobile-sdk:1.9'
	}
	```
	
	- Pubmatic SDKを使用する場合は以下を追加してください。
	
	```
	dependencies {
		//  To integrate  Geniee Wrapper Ad Adapter
		implementation 'jp.co.geniee.gnhbadapter:GNHBPubmaticAdapter:1.0.0.0'
			
		// To integrate OpenWrap SDK
		implementation 'com.pubmatic.sdk:openwrap:1.7.2'
	}
	```

2. `Sync Now`で各ライブラリを取得します。

#### 4.2. AndroidManifestの設定
1. AndroidManifestで以下のPermissionを設定します。

	```
	<uses-permission android:name="android.permission.INTERNET"/>
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	<!--
	Ask this permission to user (at runtime from code) only for API 30+
	-->
	<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
	```

2. Google Ad Managerで発行されたアプリケーションIDを`Android Manifest.xml`に記載します。指定しない場合、起動時アプリがクラッシュします。

	```
	<manifest>
		<application>
		<meta-data
			android:name="com.google.android.gms.ads.APPLICATION_ID"
			android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
		</application>
	</manifest>
	```
	
#### 4.3. RemoteConfig初期設定
1. xmlファイルを作成し、default情報(広告枠情報)について記載します。これはFirebaseのRemote Config機能から広告枠情報を取得できない場合に使用されます。

	広告枠情報としては以下のjson形式の文字列で指定します。
	
	| 第１階層 | 第2階層 | 型 | 概要 |
	| :-- | :-- | :-- | :-- |
	| unit_id |  | 文字列 | AdManagerで使用するUnitId。 |
	| timeout |  | 数値 | 各アダプターでの要求待ち時間(秒)。 |
	| is_refresh |  | Boolean | リフレッシュ機能の有効/無効。 |
	| refresh_interval |  | 数値 | リフレッシュ時間(秒)。 |
	| use_upr |  | Boolean | UPRの有効/無効。 |
	| upr_settings |  | Dictionary |  |
	|  | upr_key | 文字列 | UPRキー。 |
	|  | upr_value | 文字列 | UPR値。 |
	| use_hb |  | Boolean | HeaderBiddingの有効/無効。 |
	| hb_list |  | リスト |  |
	|  | hb_name | 文字列 | 使用するHB名。 |
	|  | hb_values | 文字列	 | 情報を取得する為の文字列。 |
	
	hb_name="Prebid"の場合の"hb\_values"
	
	| 第１階層 | 型 | 概要 |
	| :-- | :-- | :-- |
	| prebid_server_host_type | 文字列 | Prebid情報(server_host_type)。  <br>"APPNEXUS"<br>"RUBICON"<br>"CUSTOM" |
	| prebid_server_host_url | 文字列 | Prebid情報(server_host_url)。 |
	| prebid_server_account_id | 文字列 | Prebid情報(server_account_id)。 |
	| config_id | 文字列 | Prebid情報(config_id)。 |
	| ad_size | 文字列 | 広告サイズ。<br>幅x高さ |
	
	hb_name="Pubmatic"の場合の"hb\_value"
	
	| 第１階層 | 型 | 概要 |
	| :-- | :-- | :-- |
	| app_store_url | 文字列 | Pubmatic情報(app_store_url)。 |
	| pub_id | 文字列 | Pubmatic情報(pub_id)。 |
	| profile_id | 文字列 | Pubmatic情報(profile_id)。 |
	| open_wrap_ad_unit_id | 文字列 | Pubmatic情報(UnitId)。 |
	| ad_size | 文字列 | 広告サイズ。<br>幅x高さ |
	
	サンプルコード	
	
	```
	<?xml version="1.0" encoding="utf-8"?>
	<defaultsMap>
	    <entry>
	        <key>GNWrapperConfig_Android</key>
	        <value>
	            {
					  "unit_id": "/15671365/pm_sdk/PMSDK-Demo-App-Banner",
					  "timeout": 3.2,
					  "is_refresh": true,
					  "refresh_interval": 30,
					  "use_upr": true,
					  "upr_settings": {
					    "upr_key": "geniee-upr",
					    "upr_value": "prod"
					  },
					  "use_hb": true,
					  "hb_list": [
					    {
					      "hb_name": "Prebid",
					      "hb_values": {
					        "prebid_server_host_type": "APPNEXUS",
					        "prebid_server_host_url": "",
					        "prebid_server_account_id": "bfa84af2-bd16-4d35-96ad-31c6bb888df0",
					        "config_id": "6ace8c7d-88c0-4623-8117-75bc3f0a2e45",
					        "use_geo_location": "false",
					        "ad_size": "300x250"
					      }
					    },
					    {
					      "hb_name": "Pubmatic",
					      "hb_values": {
					        "app_store_url": "https://play.google.com/store/apps/details?id=com.example.android&hl=en",
					        "pub_id": "156276",
					        "profile_id": "1165",
					        "open_wrap_ad_unit_id": "/15671365/pm_sdk/PMSDK-Demo-App-Banner",
					        "ad_size": "320x50"
					      }
					    }
					  ]
					}
	        </value>
	    </entry>
	</defaultsMap>
	```
	
	Firebase RemoteConfigの初期設定については以下のURLを参考にしてください。  
	[アプリ内デフォルト パラメータ値を設定する](https://firebase.google.com/docs/remote-config/use-config-android)

#### 4.4. 実装

このSDKはそれぞれ以下のSDKの実装手順を参考に作成しております。
各SDKの詳細は以下をご覧ください。

| SDK| リンク |
| :--  | :-- |
| Firebase| [リンク](https://firebase.google.com/docs/guides?hl=ja) |
| Google Ad Manager| [リンク](https://developers.google.com/ad-manager/mobile-ads-sdk/android/quick-start?hl=ja) |
| Prebid| [リンク](https://docs.prebid.org/prebid-mobile/prebid-mobile-pbs.html) |
| Pubmatic| [リンク](https://community.pubmatic.com/display/OB/OpenWrap+SDK+Home) |



##### 4.4.1 レイアウトを設定する
1. Bannerを表示する場所のlayoutファイルに`FrameLayout`を追加し、idやstyleを適用します。`layout_width `と`layout_height`はBannerのサイズに合わせて設定します。

	- 下記は`activity_main.xml`にBannerサイズ320x50を設定する場合の例
 
	```
	<?xml version="1.0" encoding="utf-8"?>
	<androidx.constraintlayout.widget.ConstraintLayout
		xmlns:android="http://schemas.android.com/apk/res/android"
		xmlns:tools="http://schemas.android.com/tools"
		xmlns:app="http://schemas.android.com/apk/res-auto"
		android:id="@+id/constraintLayout"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		tools:context=".MainActivity">
	
		<FrameLayout
		    android:id="@+id/ad_view_layout"
		    android:layout_width="320dp"
		    android:layout_height="50dp"
		    app:layout_constraintStart_toStartOf="parent"
		    app:layout_constraintEnd_toEndOf="parent"
		    app:layout_constraintTop_toTopOf="parent"
		    app:layout_constraintBottom_toBottomOf="parent" />
	
	</androidx.constraintlayout.widget.ConstraintLayout>
	```
	
##### 4.4.2. 初期化処理
1. アプリケーション起動後処理(AppDelegate)にFirebaseの初期化処理を追加します。
	
	```kotlin
	MobileAds.initialize(this) { }
	```
	
	- prebidを使用する場合

	```	
	PrebidMobile.setApplicationContext(applicationContext)
	```


2. テスト用に使用する際は初期処理を行う際に以下の設定を行なってください。尚、下記の設定はテストモードとして動作するため、<b>リリース時には必ず外してください。</b>
	
	```kotlin
	val testDeviceIds = listOf("ADD_YOUR_TEST_DEVICE_ID")
	val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
	MobileAds.setRequestConfiguration(configuration)
	
	/*
	// Make this setting to run the GNWrapperAdSDK in test mode.
	// TODO: Be sure to remove it when releasing
	*/
	GNWrapperAdSDK.setTestMode(true)
	```
	
3. GNWrapperAdSDKをデバッグする場合は以下の処理を入れてください。
	```
	// Change the GNLogLevel if you want logcat to display the GNWrapperAdSDK debug code
	GNWrapperAdSDK.setLogLevel(GNLogLevel.INFO)

	```
		
##### 4.4.3. Bannerの初期設定

1. 4.5.1で設定したレイアウトを取得します。

	```
	val adView = findViewById<FrameLayout>(R.id.ad_view_layout)
	```
	
2. 広告オブジェクトの作成

	`AdManagerAdView`と`GNWrapperAdBanner`のオブジェクトを作成し、4.5.1で作成したレイアウトにaddViewします。

	```
	adManagerAdView = AdManagerAdView(this)
	adManagerAdView!!.appEventListener = this
	adView.addView(adManagerAdView)
	
	gnWrapperAdBanner = GNWrapperAdBanner(this)
	```
		
##### 4.4.4 Listerクラスの処理
1. `GNWrapperAdBannerListener`のListenerクラスを`gnWrapperAdBanner`に登録します。

	```
	class MainActivity : AppCompatActivity(), GNWrapperAdBannerListener {		
		gnWrapperAdBanner = GNWrapperAdBanner(this)
		gnWrapperAdBanner!!.setGnWrapperAdBannerListener(this)
		adView.addView(gnWrapperAdBanner)
	}
	```

2. Listenerクラスの処理を以下のように記載し, gnCustomTargetingParamsから以下の情報を取得して以下の設定を行います。
	- AdUnit id
	- `AdManagerAdView`の広告サイズ(こちらはフォーマットに合わせて手動で設定をお願いします)
	- `AdRequestBuilder`に対して、`CustomTargeting`の設定

	```
	override fun onComplete(adUnitId: String?, gnCustomTargetingParams: GNCustomTargetingParams) {
	    if (adManagerAdView!!.adUnitId == null) {
	        adManagerAdView!!.adUnitId = adUnitId
	        adManagerAdView!!.setAdSizes(AdSize.BANNER)
	    }
	    val adRequestBuilder = AdManagerAdRequest.Builder()
	    for (key in gnCustomTargetingParams.keys) {
	        Log.d(TAG, "key: " + key + " value: " + gnCustomTargetingParams[key])
	        adRequestBuilder.addCustomTargeting(key, gnCustomTargetingParams[key])
	    }
	    val adRequest = adRequestBuilder.build()
	    adManagerAdView!!.loadAd(adRequest)
	}
	
	override fun onError(gnErrorCode: GNErrorCode) {
	    Log.w(TAG, "GNWrapperError: " + gnErrorCode.message)
	}
	```

##### 4.4.5 Firebase処理
詳細なFirebaseのRemoteConfig機能については以下のURLを参考してください。

参考サイト：[Firebase Remote ConfigをAndroidで使用する](https://firebase.google.com/docs/remote-config/use-config-android?hl=ja)

1. 以下の初期処理を行います。
	- RemoteConfigオブジェクトの取得
	- RemoteConfig機能で情報が取得できなかった場合のdefault値指定(`remoteConfig.setDefaults`処理で、xmlファイル(3.1.で作成)を指定します)  
	-  RemoteConfigオブジェクトの取得ができた場合に`gnWrapperAdBanner`の`load`を実行する
	
	```kotlin
	firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
	val configSettings = FirebaseRemoteConfigSettings.Builder()
	        .setMinimumFetchIntervalInSeconds(3600)
	        .build()
	firebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
	firebaseRemoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
	firebaseRemoteConfig!!.fetchAndActivate()
	        .addOnCompleteListener(this) { task ->
	            if (task.isSuccessful) {
	                Log.d(TAG, "FirebaseRemoteConfig is Success")
	                val gnGNRemoteConfigValue = firebaseRemoteConfig!!.getString(GN_WRAPPER_CONFIG)
	                Log.d(TAG, "gnGNRemoteConfigValue: $gnGNRemoteConfigValue")
	                
	                // Ad Load
	                gnWrapperAdBanner!!.load(gnGNRemoteConfigValue)
	            } else {
	                Log.w(TAG, "FirebaseRemoteConfig is failed Exception: " + task.exception)
	            }
	        }
	
	```
	
##### 4.4.6 ライフサイクルイベントの登録
1. `onDestroy`のライフサイクルイベントに以下を設定してください。

	```
	override fun onDestroy() {
	    super.onDestroy()
	    if (adManagerAdView != null) {
	        adManagerAdView!!.destroy()
	    }
	    if (gnWrapperAdBanner != null) {
	        gnWrapperAdBanner!!.destroy()
	    }
	}
	```
	
##### 4.4.7 各HeaderBiddingSDK用の処理
それぞれ使用するSDKに応じて以下の処理をしてください。

######  Prebid用の処理

1. <b>Prebid SDKを使用する場合</b>はAdManagerのAdListenerクラスを生成し、`onAdLoaded `の中に以下の処理を入れてください

	```
	adManagerAdView!!.adListener = object : AdListener() {
	    override fun onAdLoaded() {
	        Log.d(TAG, "onAdLoaded")
	
	        adManagerAdView!!.visibility = View.VISIBLE
	        AdViewUtils.findPrebidCreativeSize(adManagerAdView, object : PbFindSizeListener {
	            override fun success(width: Int, height: Int) {
	                Log.d(TAG, "Resize Ad: width: $width height: $height")
	                adManagerAdView!!.setAdSizes(AdSize(width, height))
	            }
	
	            override fun failure(error: PbFindSizeError) {
	                Log.d("MyTag", "error: $error")
	            }
	        })
	    }
	
	    override fun onAdFailedToLoad(adError: LoadAdError) {
	        Log.d(TAG, "onAdFailedToLoad")
	    }
	
	    override fun onAdOpened() {
	        Log.d(TAG, "onAdOpened")
	    }
	
	    override fun onAdClicked() {
	        Log.d(TAG, "onAdClicked")
	    }
	
	    override fun onAdClosed() {
	        Log.d(TAG, "onAdClosed")
	    }
	}
	
	```

######  Pubmatic用の処理

3. <b>Pubmatic SDKを使用する場合</b>はAdManagerのAppEventListenerクラスを登録し、`onAppEvent `の中に以下の処理を入れてください

	```
	class MainActivity : AppCompatActivity(), GNWrapperAdBannerListener, AppEventListener {
	override fun onAppEvent(s: String, s1: String) {
	    if (s == "pubmaticdm") {
	        Log.d(TAG, " GNWrapperAd.isShowInSDKView: " + gnWrapperAdBanner!!.isShowInSDKView)
	        if (gnWrapperAdBanner!!.isShowInSDKView) {
	            adManagerAdView!!.visibility = View.INVISIBLE
	            gnWrapperAdBanner!!.show()
	        }
	    }
	}
	```



## 備考
- 一連の実装コードのついては、サンプルアプリを参考にしてください。
