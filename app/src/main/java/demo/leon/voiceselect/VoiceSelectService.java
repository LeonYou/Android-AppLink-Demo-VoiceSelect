package demo.leon.voiceselect;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.interfaces.IProxyListenerALM;
import com.ford.syncV4.proxy.rpc.AddCommandResponse;
import com.ford.syncV4.proxy.rpc.AddSubMenuResponse;
import com.ford.syncV4.proxy.rpc.AlertResponse;
import com.ford.syncV4.proxy.rpc.ChangeRegistrationResponse;
import com.ford.syncV4.proxy.rpc.Choice;
import com.ford.syncV4.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteCommandResponse;
import com.ford.syncV4.proxy.rpc.DeleteFileResponse;
import com.ford.syncV4.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteSubMenuResponse;
import com.ford.syncV4.proxy.rpc.DiagnosticMessageResponse;
import com.ford.syncV4.proxy.rpc.EndAudioPassThruResponse;
import com.ford.syncV4.proxy.rpc.GenericResponse;
import com.ford.syncV4.proxy.rpc.GetDTCsResponse;
import com.ford.syncV4.proxy.rpc.GetVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.ListFilesResponse;
import com.ford.syncV4.proxy.rpc.OnAudioPassThru;
import com.ford.syncV4.proxy.rpc.OnButtonEvent;
import com.ford.syncV4.proxy.rpc.OnButtonPress;
import com.ford.syncV4.proxy.rpc.OnCommand;
import com.ford.syncV4.proxy.rpc.OnDriverDistraction;
import com.ford.syncV4.proxy.rpc.OnHMIStatus;
import com.ford.syncV4.proxy.rpc.OnHashChange;
import com.ford.syncV4.proxy.rpc.OnKeyboardInput;
import com.ford.syncV4.proxy.rpc.OnLanguageChange;
import com.ford.syncV4.proxy.rpc.OnLockScreenStatus;
import com.ford.syncV4.proxy.rpc.OnPermissionsChange;
import com.ford.syncV4.proxy.rpc.OnSystemRequest;
import com.ford.syncV4.proxy.rpc.OnTBTClientState;
import com.ford.syncV4.proxy.rpc.OnTouchEvent;
import com.ford.syncV4.proxy.rpc.OnVehicleData;
import com.ford.syncV4.proxy.rpc.PerformAudioPassThruResponse;
import com.ford.syncV4.proxy.rpc.PerformInteractionResponse;
import com.ford.syncV4.proxy.rpc.PutFileResponse;
import com.ford.syncV4.proxy.rpc.ReadDIDResponse;
import com.ford.syncV4.proxy.rpc.ResetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.ScrollableMessageResponse;
import com.ford.syncV4.proxy.rpc.SetAppIconResponse;
import com.ford.syncV4.proxy.rpc.SetDisplayLayoutResponse;
import com.ford.syncV4.proxy.rpc.SetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.SetMediaClockTimerResponse;
import com.ford.syncV4.proxy.rpc.ShowResponse;
import com.ford.syncV4.proxy.rpc.SliderResponse;
import com.ford.syncV4.proxy.rpc.SoftButton;
import com.ford.syncV4.proxy.rpc.SpeakResponse;
import com.ford.syncV4.proxy.rpc.SubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.SubscribeVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.SystemRequestResponse;
import com.ford.syncV4.proxy.rpc.UnsubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.enums.InteractionMode;
import com.ford.syncV4.proxy.rpc.enums.SoftButtonType;
import com.ford.syncV4.proxy.rpc.enums.SyncDisconnectedReason;
import com.ford.syncV4.proxy.rpc.enums.SystemAction;

import java.util.Arrays;
import java.util.Vector;

// InteractionChoiceSet Service
public class VoiceSelectService extends Service implements IProxyListenerALM
{
	private SyncProxyALM mProxy = null;
	private static ComponentName mComName = null;
	private static final int ID_BTN_TEST = 1000;
	private static final int[] ID_CHOICE_SET =
			{
				2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009,
			};
	private static final int[] ID_CHOICE =
		{
				3000, 3001, 3002, 3003, 3004, 3005, 3006, 3007, 3008, 3009,
				3010, 3011, 3012, 3013, 3014, 3015, 3016, 3017, 3018, 3019,
		};

	private static final int ID_PAGE_UP = 4001;
	private static final int ID_PAGE_DOWN = 4002;
	private static final int PAGE_SIZE = 4;
	private static final int TOTAL_PAGE = ID_CHOICE.length / PAGE_SIZE;
	private static final String CHOICE_NAME = "测试选项页";
	private static final String PAGE_UP_NAME = "上一页";
	private static final String PAGE_DOWN_NAME = "下一页";
	public static final String SERVICE_NANE = "com.example.myapplink.TestService";
	public int mCurrentPage = 0;

	public static void startService(Context ctx)
	{
		if (mComName == null)
		{
			Intent intent = new Intent(SERVICE_NANE);
			mComName = ctx.startService(intent);
		}
	}

	public static void stopService(Context ctx)
	{
		if (mComName != null)
		{
			Intent intent = new Intent(SERVICE_NANE);
			ctx.stopService(intent);
			mComName = null;
		}
	}

	private void startProxy()
	{
		if (mProxy == null)
		{
			try
			{
				mProxy = new SyncProxyALM(this, "福特测试", true, "584421907");
			}
			catch (SyncException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void disposeProxy()
	{
		if (mProxy != null)
		{
			try
			{
				mProxy.dispose();
				mProxy = null;
			}
			catch (SyncException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void resetProxy()
	{
		if (mProxy != null)
		{
			try
			{
				mProxy.resetProxy();
			}
			catch (SyncException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void createPerformingAction()
	{
		Choice choicePageup = new Choice();
		choicePageup.setChoiceID(ID_PAGE_UP);
		choicePageup.setMenuName(PAGE_UP_NAME);
		choicePageup.setVrCommands(new Vector<String>(Arrays.asList(PAGE_UP_NAME)));

		Choice choicePagedown = new Choice();
		choicePagedown.setChoiceID(ID_PAGE_DOWN);
		choicePagedown.setMenuName(PAGE_DOWN_NAME);
		choicePagedown.setVrCommands(new Vector<String>(Arrays.asList(PAGE_DOWN_NAME)));


		for (int i=0; i<TOTAL_PAGE; i++)
		{
			Vector<Choice> vec = new Vector<Choice>();
			for (int j=0; j < PAGE_SIZE; j++)
			{
				String name = String.format("%s%d", CHOICE_NAME, j+i*PAGE_SIZE+1);
				Choice c = new Choice();
				c.setChoiceID(ID_CHOICE[j+i*PAGE_SIZE]);
				c.setMenuName(name);
				c.setVrCommands(new Vector<String>(Arrays.asList(name)));
				vec.add(c);
			}

			if (i > 0)
			{
				vec.add(choicePageup);
			}

			if (i < TOTAL_PAGE -1)
			{
				vec.add(choicePagedown);
			}


			try
			{
				mProxy.createInteractionChoiceSet(vec, ID_CHOICE_SET[i], 0);
			}
			catch (SyncException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void showPerformingAction(int page)
	{
		final String initPrompt = String.format("列表%d", page + 1);
		final String display = String.format("测试%d", page + 1);

		try
		{
			mProxy.performInteraction(initPrompt, display, ID_CHOICE_SET[page], null, null, InteractionMode.VR_ONLY, 10000, 0);
		}
		catch (SyncException e)
		{
			e.printStackTrace();
		}
	}

	public VoiceSelectService()
	{
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		startProxy();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		disposeProxy();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}


	@Override
	public void onOnHMIStatus(OnHMIStatus status)
	{
		switch (status.getSystemContext())
		{
		case SYSCTXT_MAIN:
			break;
		case SYSCTXT_VRSESSION:
			break;
		case SYSCTXT_MENU:
			break;
		default:
			return;
		}

		switch (status.getAudioStreamingState())
		{
		case AUDIBLE:
			// play audio if applicable
			break;
		case NOT_AUDIBLE:
			// pause/stop/mute audio if applicable
			break;
		default:
			return;
		}

		switch (status.getHmiLevel())
		{
		case HMI_FULL:
			int id = 0;
			if (status.getFirstRun())
			{
				// setup app on SYNC
				// send welcome message if applicable
				try
				{
					SoftButton sb1 = new SoftButton();
					sb1.setSoftButtonID(ID_BTN_TEST);
					sb1.setText("List");
					sb1.setType(SoftButtonType.SBT_TEXT);
					sb1.setSystemAction(SystemAction.DEFAULT_ACTION);

					Vector<SoftButton> btns = new Vector<SoftButton>();
					btns.add(sb1);

					createPerformingAction();

					mProxy.show("Initializing", null, null, null, null, null,
								null, null, btns, null, null, id);

				}
				catch (SyncException e)
				{
					e.printStackTrace();
				}


			}
			else
			{

			}
			break;
		case HMI_LIMITED:
			break;
		case HMI_BACKGROUND:
			break;
		case HMI_NONE:
			break;
		default:
			return;
		}
	}

	@Override
	public void onProxyClosed(String s, Exception e, SyncDisconnectedReason syncDisconnectedReason)
	{

	}

	@Override
	public void onError(String s, Exception e)
	{

	}

	@Override
	public void onGenericResponse(GenericResponse genericResponse)
	{

	}

	@Override
	public void onOnCommand(OnCommand onCommand)
	{

	}

	@Override
	public void onAddCommandResponse(AddCommandResponse addCommandResponse)
	{

	}

	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse addSubMenuResponse)
	{

	}

	@Override
	public void onCreateInteractionChoiceSetResponse(
			CreateInteractionChoiceSetResponse createInteractionChoiceSetResponse)
	{

	}

	@Override
	public void onAlertResponse(AlertResponse alertResponse)
	{

	}

	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse deleteCommandResponse)
	{

	}

	@Override
	public void onDeleteInteractionChoiceSetResponse(
			DeleteInteractionChoiceSetResponse deleteInteractionChoiceSetResponse)
	{

	}

	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse deleteSubMenuResponse)
	{

	}

	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse response)
	{
		String result = response.getResultCode().toString();
		String info = response.getInfo();
		if (response.getSuccess())
		{
			switch (response.getChoiceID())
			{
			case ID_PAGE_UP:
				if (mCurrentPage > 0)
				{
					showPerformingAction(--mCurrentPage);
				}
				break;
			case ID_PAGE_DOWN:
				if (mCurrentPage < TOTAL_PAGE - 1)
				{
					showPerformingAction(++mCurrentPage);
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse resetGlobalPropertiesResponse)
	{

	}

	@Override
	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse setGlobalPropertiesResponse)
	{

	}

	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse setMediaClockTimerResponse)
	{

	}

	@Override
	public void onShowResponse(ShowResponse showResponse)
	{

	}

	@Override
	public void onSpeakResponse(SpeakResponse speakResponse)
	{

	}

	@Override
	public void onOnButtonEvent(OnButtonEvent onButtonEvent)
	{

	}

	@Override
	public void onOnButtonPress(OnButtonPress notification)
	{
		switch(notification.getCustomButtonName())
		{
		case ID_BTN_TEST:
			mCurrentPage = 0;
			showPerformingAction(mCurrentPage);
			break;
		default:
			break;
		}

	}

	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse subscribeButtonResponse)
	{

	}

	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse unsubscribeButtonResponse)
	{

	}

	@Override
	public void onOnPermissionsChange(OnPermissionsChange onPermissionsChange)
	{

	}

	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse subscribeVehicleDataResponse)
	{

	}

	@Override
	public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse unsubscribeVehicleDataResponse)
	{

	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse getVehicleDataResponse)
	{

	}

	@Override
	public void onOnVehicleData(OnVehicleData onVehicleData)
	{

	}

	@Override
	public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response)
	{

	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse endAudioPassThruResponse)
	{

	}

	@Override
	public void onOnAudioPassThru(OnAudioPassThru onAudioPassThru)
	{

	}

	@Override
	public void onPutFileResponse(PutFileResponse putFileResponse)
	{

	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse deleteFileResponse)
	{

	}

	@Override
	public void onListFilesResponse(ListFilesResponse listFilesResponse)
	{

	}

	@Override
	public void onSetAppIconResponse(SetAppIconResponse setAppIconResponse)
	{

	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse scrollableMessageResponse)
	{

	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse changeRegistrationResponse)
	{

	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse setDisplayLayoutResponse)
	{

	}

	@Override
	public void onOnLanguageChange(OnLanguageChange onLanguageChange)
	{

	}

	@Override
	public void onOnHashChange(OnHashChange onHashChange)
	{

	}

	@Override
	public void onSliderResponse(SliderResponse sliderResponse)
	{

	}

	@Override
	public void onOnDriverDistraction(OnDriverDistraction onDriverDistraction)
	{

	}

	@Override
	public void onOnTBTClientState(OnTBTClientState onTBTClientState)
	{

	}

	@Override
	public void onOnSystemRequest(OnSystemRequest onSystemRequest)
	{

	}

	@Override
	public void onSystemRequestResponse(SystemRequestResponse systemRequestResponse)
	{

	}

	@Override
	public void onOnKeyboardInput(OnKeyboardInput onKeyboardInput)
	{

	}

	@Override
	public void onOnTouchEvent(OnTouchEvent onTouchEvent)
	{

	}

	@Override
	public void onDiagnosticMessageResponse(DiagnosticMessageResponse diagnosticMessageResponse)
	{

	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse readDIDResponse)
	{

	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse getDTCsResponse)
	{

	}

	@Override
	public void onOnLockScreenNotification(OnLockScreenStatus onLockScreenStatus)
	{

	}
}
