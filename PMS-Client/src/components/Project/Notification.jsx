import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useQuery, useMutation } from "@apollo/client";
import { FIND_PROJECT_BY_ID } from "@/graphql/Queries/project-service";
import { UPDATE_NOTIFICATION } from "@/graphql/Mutation/project-service";
import { useApolloClients } from "@/graphql/Clients/ApolloClientContext";

function Notification() {
  const { projectId } = useParams();
  const { projectClient } = useApolloClients();
  const [notificationOn, setNotificationOn] = useState(false);

  const { data, loading, error, refetch } = useQuery(FIND_PROJECT_BY_ID, {
    client: projectClient,
    variables: { projectId },
    skip: !projectId,
    fetchPolicy: "network-only", // Ensure fresh data
  });

  const [updateNotification] = useMutation(UPDATE_NOTIFICATION, {
    client: projectClient,
  });

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
      refetch(); // Refresh project data
    } catch (err) {
      console.error("Failed to update notification:", err.message);
    }
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p className="text-red-500">Error: {error.message}</p>;

  const project = data?.getProject;

  return (
    <div className="p-6 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">
      <h2 className="text-2xl md:text-3xl font-semibold text-gray-700 mb-4">
        Project Update Settings
      </h2>

      <p className="mb-4 text-sm sm:text-lg md:text-xl text-gray-800">
        Turn the project update notifications <strong>ON</strong> or <strong>OFF</strong> to 
        notify project members of changes to project settings.
      </p>

      <div className="flex flex-col sm:flex-row justify-between items-center">
        <p className="text-sm sm:text-base md:text-lg text-gray-700 mb-4 sm:mb-0">
          Notifications for <strong>{project?.title}</strong> are currently{" "}
          <span className={notificationOn ? "text-green-600" : "text-red-500"}>
            {notificationOn ? "ON" : "OFF"}
          </span>
        </p>

        <button
          onClick={handleToggle}
          className={`w-full sm:w-auto px-6 py-3 rounded-md text-white ${
            notificationOn ? "bg-red-500 hover:bg-red-600" : "bg-blue-500 hover:bg-blue-600"
          } transition duration-300 ease-in-out transform hover:scale-105`}
        >
          {notificationOn ? "Turn Off" : "Turn On"}
        </button>
      </div>

      <div className="mt-6 text-sm md:text-base text-gray-600">
        <p className="italic">
          When notifications are turned on, project members will receive updates for important project changes.
        </p>
      </div>
    </div>
  );
}

export default Notification;
