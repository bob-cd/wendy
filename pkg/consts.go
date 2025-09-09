package pkg

type Kind string
type EventType string

const (
	Pipeline         Kind = "Pipeline"
	ResourceProvider Kind = "ResourceProvider"
	ArtifactStore    Kind = "ArtifactStore"
	Logger           Kind = "Logger"

	Normal  EventType = "Normal"
	Warning EventType = "Warning"
	Error   EventType = "Error"
)

type Event struct {
	At      int64     `json:"at"`
	Kind    Kind      `json:"kind"`
	Type    EventType `json:"type"`
	Reason  string    `json:"reason"`
	Message string    `json:"message"`
}
