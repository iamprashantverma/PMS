import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useQuery, useMutation } from "@apollo/client";
import { FIND_PROJECT_BY_ID } from "@/graphql/Queries/project-service";
import { UPDATE_NOTIFICATION } from "@/graphql/Mutation/project-service";

function Notification() {
  const { projectId } = useParams();

  const { data, loading, error, refetch } = useQuery(FIND_PROJECT_BY_ID, {
    variables: { projectId },
    skip: !projectId,
  });

  const [updateNotification] = useMutation(UPDATE_NOTIFICATION);
  const [notificationOn, setNotificationOn] = useState(false);

  useEffect(() => {
    if (data?.getProject?.notification !== undefined) {
      setNotificationOn(data.getProject.notification);
    }
  }, [data]);

  const handleToggle = async () => {
    const newFlag = !notificationOn;
    try {
      await updateNotification({
        variables: {
          projectId,
          flag: newFlag,
        },
      });
      setNotificationOn(newFlag);
      refetch();
    } catch (err) {
      console.error("Failed to update notification:", err);
    }
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p className="text-red-500">Error loading project.</p>;

  return (
    <div className="p-6 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">
      <h2 className="text-2xl md:text-3xl font-semibold text-gray-700 mb-4">
        Project Update Settings
      </h2>
      
      <p className="mb-4 text-sm sm:text-lg md:text-xl text-gray-800">
        Turn the project update notifications <strong>ON</strong> or <strong>OFF</strong> to 
        notify project members of any changes in the project settings. This includes changes 
        to project details, status, or any significant updates that may affect the project as a whole.
      </p>

      <div className="flex flex-col sm:flex-row justify-between items-center">
        <p className="text-sm sm:text-base md:text-lg text-gray-700 mb-4 sm:mb-0">
          Project update notifications for <strong>{data?.getProject?.title}</strong> are currently{" "}
          <span className={notificationOn ? "text-green-600" : "text-red-500"}>
            {notificationOn ? "ON" : "OFF"}
          </span>
        </p>
        
        <button
          onClick={handleToggle}
          className={`w-full sm:w-auto px-6 py-3 rounded-md text-white ${
            notificationOn ? "bg-red-500 hover:bg-red-600" : "bg-blue-500 hover:bg-blue-600"
          } transition duration-300 ease-in-out transform hover:scale-105`}
          title={notificationOn ? "Turn off project update notifications" : "Turn on project update notifications"}
        >
          {notificationOn ? "Turn Off" : "Turn On"}
        </button>
      </div>

      <div className="mt-6 text-sm md:text-base text-gray-600">
        <p className="italic">
          When project update notifications are turned on, project members will receive notifications 
          whenever there are changes to the project settings, such as the project status, deadlines, 
          or other important updates that affect the overall project.
        </p>
      </div>
    </div>
  );
}

export default Notification;
